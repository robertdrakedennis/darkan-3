// Darkan Launcher - Frontend
(function () {
  "use strict";

  let state = {
    config: null,
    sessions: [], // [{user_id, display_name, accounts, session_id}]
    launching: false,
    clientsVisible: false,
    clients: [], // [{pid, state, version, reloads}]
    clientsPollTimer: null,
    busyPids: {}, // pid -> true while an action is in flight
  };

  // DOM elements
  const $ = (s) => document.querySelector(s);
  const noAccounts = $("#no-accounts");
  const accountPanel = $("#account-panel");
  const selAccount = $("#sel-account");
  const selCharacter = $("#sel-character");
  const btnPlay = $("#btn-play");
  const btnLogin = $("#btn-login");
  const btnAddAccount = $("#btn-add-account");
  const btnLogout = $("#btn-logout");
  const btnSettings = $("#btn-settings");
  const btnTheme = $("#btn-theme");
  const btnCloseSettings = $("#btn-close-settings");
  const btnSaveSettings = $("#btn-save-settings");
  const settingsModal = $("#settings-modal");
  const statusLog = $("#status-log");
  const customFields = $("#custom-fields");
  const chkCloseAfter = $("#chk-close-after");
  const chkAutoInject = $("#chk-auto-inject");
  const inpLaunchCmd = $("#inp-launch-cmd");
  const inpHost = $("#inp-host");
  const inpPort = $("#inp-port");
  const btnClients = $("#btn-clients");
  const btnCloseClients = $("#btn-close-clients");
  const btnRefreshClients = $("#btn-refresh-clients");
  const playView = $("#play-view");
  const clientsView = $("#clients-view");
  const clientsList = $("#clients-list");
  const clientsEmpty = $("#clients-empty");

  const CLIENTS_POLL_MS = 3000;

  // IPC
  function send(msg) {
    window.ipc.postMessage(JSON.stringify(msg));
  }

  function getSelectedMode() {
    const radio = document.querySelector('input[name="mode"]:checked');
    return radio ? radio.value : "Live";
  }

  function setSelectedMode(mode) {
    const radio = document.querySelector(`input[name="mode"][value="${mode}"]`);
    if (radio) radio.checked = true;
  }

  function isCustomMode() {
    return getSelectedMode() === "Custom";
  }

  function showServerFields() {
    return isCustomMode();
  }

  // Receive events from Rust
  window.__darkan_callback = function (event) {
    switch (event.type) {
      case "init":
        state.config = event.config;
        state.sessions = event.sessions;
        applyTheme();
        restoreConfigToUI();
        updateUI();
        break;

      case "login_complete":
        state.sessions.push(event.session);
        updateUI();
        logStatus("Login successful", "success");
        break;

      case "login_error":
        logStatus(event.message, "error");
        break;

      case "logout_complete":
        state.sessions = state.sessions.filter(
          (s) => s.user_id !== event.user_id
        );
        updateUI();
        logStatus("Logged out");
        break;

      case "launch_status":
        state.launching = event.message !== "Game launched!";
        btnPlay.disabled = state.launching;
        updatePlayButton();
        logStatus(
          event.message,
          event.message === "Game launched!" ? "success" : ""
        );
        break;

      case "launch_error":
        state.launching = false;
        btnPlay.disabled = false;
        updatePlayButton();
        logStatus(event.message, "error");
        break;

      case "config_saved":
        logStatus("Settings saved", "success");
        break;

      case "clients_list":
        state.clients = event.clients || [];
        renderClients();
        break;

      case "client_control_result":
        // The action finished; clear its busy flag and report.
        delete state.busyPids[event.pid];
        logStatus(
          "pid " + event.pid + ": " + event.message,
          event.ok ? "success" : "error"
        );
        // A ClientsList event follows from the backend; re-render to drop the
        // spinner immediately even before it arrives.
        renderClients();
        break;
    }
  };

  function restoreConfigToUI() {
    if (!state.config) return;

    // Restore mode selection
    const mode = state.config.server_mode || "Live";
    setSelectedMode(mode);
    customFields.style.display = mode === "Custom" ? "" : "none";

    // Restore custom server fields
    inpHost.value = state.config.custom_server_host || "localhost";
    inpPort.value = state.config.custom_server_port || 43594;

    // Restore settings modal fields
    chkCloseAfter.checked = state.config.close_after_launch;
    chkAutoInject.checked = !!state.config.auto_inject_undercut;
    inpLaunchCmd.value = state.config.custom_launch_command || "";
  }

  function updateUI() {
    const custom = isCustomMode();
    const serverFields = showServerFields();

    if (custom) {
      // Private Server mode: no accounts needed
      noAccounts.style.display = "none";
      accountPanel.style.display = "none";
      btnPlay.style.display = "";
    } else if (state.sessions.length === 0) {
      noAccounts.style.display = "";
      accountPanel.style.display = "none";
      btnPlay.style.display = "none";
    } else {
      noAccounts.style.display = "none";
      accountPanel.style.display = "";
      btnPlay.style.display = "";

      // Populate account selector
      const prevAccount = selAccount.value;
      selAccount.innerHTML = "";
      state.sessions.forEach((s) => {
        const opt = document.createElement("option");
        opt.value = s.user_id;
        opt.textContent = s.display_name;
        selAccount.appendChild(opt);
      });
      if (prevAccount && [...selAccount.options].some((o) => o.value === prevAccount)) {
        selAccount.value = prevAccount;
      }

      updateCharacters();
    }

    customFields.style.display = serverFields ? "" : "none";

    // Settings
    if (state.config) {
      chkCloseAfter.checked = state.config.close_after_launch;
      chkAutoInject.checked = !!state.config.auto_inject_undercut;
      inpLaunchCmd.value = state.config.custom_launch_command || "";
    }
  }

  function updateCharacters() {
    const session = state.sessions.find((s) => s.user_id === selAccount.value);
    selCharacter.innerHTML = "";
    if (session) {
      session.accounts.forEach((a) => {
        const opt = document.createElement("option");
        opt.value = a.accountId;
        opt.textContent = a.displayName || "Unnamed";
        selCharacter.appendChild(opt);
      });
    }
  }

  function applyTheme() {
    if (state.config && !state.config.dark_theme) {
      document.body.classList.add("light");
    } else {
      document.body.classList.remove("light");
    }
  }

  function logStatus(msg, cls) {
    const entry = document.createElement("div");
    entry.className = "log-entry" + (cls ? " " + cls : "");
    entry.textContent = "> " + msg;
    statusLog.appendChild(entry);
    statusLog.scrollTop = statusLog.scrollHeight;
  }

  function saveServerModeToConfig() {
    if (!state.config) return;
    state.config.server_mode = getSelectedMode();
    state.config.custom_server_host = inpHost.value || null;
    state.config.custom_server_port = parseInt(inpPort.value) || null;
    if (getSelectedMode() !== "Live") {
      const host = inpHost.value || "localhost";
      const port = parseInt(inpPort.value) || 8829;
      state.config.custom_config_uri = "http://" + host + ":" + port + "/jav_config.ws";
    } else {
      state.config.custom_config_uri = null;
    }
    send({ type: "save_config", config: state.config });
  }

  // ---- Clients panel (Undercut engine hot-reload control) ----

  function openClients() {
    state.clientsVisible = true;
    playView.style.display = "none";
    clientsView.style.display = "";
    requestClients();
    startClientsPolling();
  }

  function closeClients() {
    state.clientsVisible = false;
    clientsView.style.display = "none";
    playView.style.display = "";
    stopClientsPolling();
  }

  function requestClients() {
    send({ type: "list_clients" });
  }

  function startClientsPolling() {
    stopClientsPolling();
    state.clientsPollTimer = setInterval(() => {
      if (state.clientsVisible) requestClients();
    }, CLIENTS_POLL_MS);
  }

  function stopClientsPolling() {
    if (state.clientsPollTimer !== null) {
      clearInterval(state.clientsPollTimer);
      state.clientsPollTimer = null;
    }
  }

  // Map an engine state to a human label + CSS modifier class.
  function clientStateMeta(s) {
    switch (s) {
      case "active":
        return { label: "Active", cls: "active" };
      case "unloaded":
        return { label: "Disabled", cls: "unloaded" };
      case "not-injected":
        return { label: "Not injected", cls: "not-injected" };
      default:
        return { label: "Error", cls: "error" };
    }
  }

  function renderClients() {
    clientsList.innerHTML = "";

    if (!state.clients.length) {
      clientsEmpty.style.display = "";
      return;
    }
    clientsEmpty.style.display = "none";

    state.clients.forEach((c) => {
      const meta = clientStateMeta(c.state);
      const busy = !!state.busyPids[c.pid];

      const row = document.createElement("div");
      row.className = "client-row";

      const info = document.createElement("div");
      info.className = "client-info";

      const top = document.createElement("div");
      top.className = "client-top";
      const badge = document.createElement("span");
      badge.className = "badge badge-" + meta.cls;
      badge.textContent = meta.label;
      const pidLabel = document.createElement("span");
      pidLabel.className = "client-pid";
      pidLabel.textContent = "pid " + c.pid;
      top.appendChild(badge);
      top.appendChild(pidLabel);

      const sub = document.createElement("div");
      sub.className = "client-sub";
      const parts = [];
      if (c.version) parts.push("v" + c.version);
      if (c.state === "active" && typeof c.reloads === "number") {
        parts.push(c.reloads + " reload" + (c.reloads === 1 ? "" : "s"));
      }
      sub.textContent = parts.join(" · ");

      info.appendChild(top);
      info.appendChild(sub);

      const actions = document.createElement("div");
      actions.className = "client-actions";

      if (c.state === "not-injected") {
        actions.appendChild(
          clientButton("Inject", "btn-primary", busy, () =>
            clientAction(c.pid, "inject_client")
          )
        );
      } else if (c.state === "active") {
        actions.appendChild(
          clientButton("Reinject", "btn-secondary", busy, () =>
            clientAction(c.pid, "reinject_client")
          )
        );
        actions.appendChild(
          clientButton("Uninject", "btn-danger", busy, () =>
            clientAction(c.pid, "uninject_client")
          )
        );
      } else if (c.state === "unloaded") {
        actions.appendChild(
          clientButton("Inject", "btn-primary", busy, () =>
            clientAction(c.pid, "inject_client")
          )
        );
      }
      // "error" rows expose no actions (the socket is unreachable).

      row.appendChild(info);
      row.appendChild(actions);
      clientsList.appendChild(row);
    });
  }

  function clientButton(label, variant, busy, onClick) {
    const b = document.createElement("button");
    b.className = "btn btn-sm " + variant;
    b.textContent = busy ? "…" : label;
    b.disabled = busy;
    b.addEventListener("click", onClick);
    return b;
  }

  function clientAction(pid, type) {
    state.busyPids[pid] = true;
    renderClients();
    send({ type: type, pid: pid });
  }

  function updatePlayButton() {
    if (state.launching) {
      btnPlay.textContent = "Launching\u2026";
      btnPlay.classList.add("launching");
    } else {
      btnPlay.innerHTML = "&#9654; Play";
      btnPlay.classList.remove("launching");
    }
  }

  // Event handlers
  btnLogin.addEventListener("click", () => send({ type: "login" }));
  btnAddAccount.addEventListener("click", () => send({ type: "login" }));

  btnLogout.addEventListener("click", () => {
    const userId = selAccount.value;
    if (userId) {
      send({ type: "logout", user_id: userId });
    }
  });

  // Unified play button — action depends on mode
  btnPlay.addEventListener("click", () => {
    if (isCustomMode()) {
      // Custom mode: no account needed, just save config and launch
      saveServerModeToConfig();
      state.launching = true;
      btnPlay.disabled = true;
      updatePlayButton();
      send({ type: "launch_custom" });
    } else {
      // Live mode: need account selection
      const accountId = selCharacter.value;
      const session = state.sessions.find((s) => s.user_id === selAccount.value);
      if (!accountId || !session) return;

      const account = session.accounts.find((a) => a.accountId === accountId);
      if (!account) return;

      if (showServerFields()) saveServerModeToConfig();

      state.launching = true;
      btnPlay.disabled = true;
      updatePlayButton();

      send({
        type: "launch",
        account_id: accountId,
        display_name: account.displayName || "",
      });
    }
  });

  selAccount.addEventListener("change", updateCharacters);

  // Mode radio
  document.querySelectorAll('input[name="mode"]').forEach((r) => {
    r.addEventListener("change", () => {
      updateUI();
      saveServerModeToConfig();
    });
  });

  // Clients panel
  btnClients.addEventListener("click", () => {
    if (state.clientsVisible) {
      closeClients();
    } else {
      openClients();
    }
  });
  btnCloseClients.addEventListener("click", closeClients);
  btnRefreshClients.addEventListener("click", requestClients);

  // Theme toggle
  btnTheme.addEventListener("click", () => {
    if (!state.config) return;
    state.config.dark_theme = !state.config.dark_theme;
    applyTheme();
    send({ type: "save_config", config: state.config });
  });

  // Settings modal
  btnSettings.addEventListener("click", () => {
    settingsModal.style.display = "";
  });
  btnCloseSettings.addEventListener("click", () => {
    settingsModal.style.display = "none";
  });
  btnSaveSettings.addEventListener("click", () => {
    if (!state.config) return;
    state.config.close_after_launch = chkCloseAfter.checked;
    state.config.auto_inject_undercut = chkAutoInject.checked;
    state.config.custom_launch_command = inpLaunchCmd.value || null;
    send({ type: "save_config", config: state.config });
    settingsModal.style.display = "none";
  });

  // Close on clicking modal backdrop
  settingsModal.addEventListener("click", (e) => {
    if (e.target === settingsModal) {
      settingsModal.style.display = "none";
    }
  });

  // Signal the Rust backend that the frontend is ready. The backend replies
  // with the "init" event (config + saved sessions). This replaces the old
  // fixed startup delay, which both added latency and could race JS readiness.
  send({ type: "ready" });
})();
