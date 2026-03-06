// Bolt RS3 Launcher - Frontend
(function () {
  "use strict";

  let state = {
    config: null,
    sessions: [], // [{user_id, display_name, accounts, session_id}]
    launching: false,
  };

  // DOM elements
  const $ = (s) => document.querySelector(s);
  const noAccounts = $("#no-accounts");
  const accountPanel = $("#account-panel");
  const selAccount = $("#sel-account");
  const selCharacter = $("#sel-character");
  const btnPlay = $("#btn-play");
  const btnPlayCustom = $("#btn-play-custom");
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
  const inpLaunchCmd = $("#inp-launch-cmd");
  const inpHost = $("#inp-host");
  const inpPort = $("#inp-port");

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

  // Receive events from Rust
  window.__bolt_callback = function (event) {
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
        btnPlayCustom.disabled = state.launching;
        logStatus(
          event.message,
          event.message === "Game launched!" ? "success" : ""
        );
        break;

      case "launch_error":
        state.launching = false;
        btnPlay.disabled = false;
        btnPlayCustom.disabled = false;
        logStatus(event.message, "error");
        break;

      case "config_saved":
        logStatus("Settings saved", "success");
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
    inpLaunchCmd.value = state.config.custom_launch_command || "";
  }

  function updateUI() {
    const custom = isCustomMode();

    if (state.sessions.length === 0) {
      noAccounts.style.display = custom ? "none" : "";
      accountPanel.style.display = "none";
    } else {
      noAccounts.style.display = "none";
      accountPanel.style.display = custom ? "none" : "";

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

    // In custom mode, hide the live account panel and show the custom play button
    // In live mode, show the account panel (if sessions exist) and hide the custom play button
    if (custom) {
      noAccounts.style.display = "none";
      accountPanel.style.display = "none";
    }

    // Settings
    if (state.config) {
      chkCloseAfter.checked = state.config.close_after_launch;
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
        opt.textContent = a.displayName;
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
    const host = inpHost.value || "localhost";
    state.config.custom_config_uri = "http://" + host + ":8080/jav_config.ws";
    send({ type: "save_config", config: state.config });
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

  // Live mode play button
  btnPlay.addEventListener("click", () => {
    const accountId = selCharacter.value;
    const session = state.sessions.find((s) => s.user_id === selAccount.value);
    if (!accountId || !session) return;

    const account = session.accounts.find((a) => a.accountId === accountId);
    if (!account) return;

    state.launching = true;
    btnPlay.disabled = true;

    send({
      type: "launch",
      account_id: accountId,
      display_name: account.displayName,
    });
  });

  // Custom mode play button
  btnPlayCustom.addEventListener("click", () => {
    // Save current custom settings before launching
    saveServerModeToConfig();

    state.launching = true;
    btnPlayCustom.disabled = true;

    send({ type: "launch_custom" });
  });

  selAccount.addEventListener("change", updateCharacters);

  // Mode radio
  document.querySelectorAll('input[name="mode"]').forEach((r) => {
    r.addEventListener("change", () => {
      customFields.style.display = r.value === "Custom" ? "" : "none";
      updateUI();
      saveServerModeToConfig();
    });
  });

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
})();
