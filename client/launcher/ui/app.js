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

  // IPC
  function send(msg) {
    window.ipc.postMessage(JSON.stringify(msg));
  }

  // Receive events from Rust
  window.__bolt_callback = function (event) {
    switch (event.type) {
      case "init":
        state.config = event.config;
        state.sessions = event.sessions;
        applyTheme();
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
        logStatus(
          event.message,
          event.message === "Game launched!" ? "success" : ""
        );
        break;

      case "launch_error":
        state.launching = false;
        btnPlay.disabled = false;
        logStatus(event.message, "error");
        break;

      case "config_saved":
        logStatus("Settings saved", "success");
        break;
    }
  };

  function updateUI() {
    if (state.sessions.length === 0) {
      noAccounts.style.display = "";
      accountPanel.style.display = "none";
      return;
    }

    noAccounts.style.display = "none";
    accountPanel.style.display = "";

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

  // Event handlers
  btnLogin.addEventListener("click", () => send({ type: "login" }));
  btnAddAccount.addEventListener("click", () => send({ type: "login" }));

  btnLogout.addEventListener("click", () => {
    const userId = selAccount.value;
    if (userId) {
      send({ type: "logout", user_id: userId });
    }
  });

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

  selAccount.addEventListener("change", updateCharacters);

  // Mode radio
  document.querySelectorAll('input[name="mode"]').forEach((r) => {
    r.addEventListener("change", () => {
      customFields.style.display = r.value === "Custom" ? "" : "none";
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
