# Ghidra MCP on macOS (port 9080)

Reproducible setup for the LaurieWired GhidraMCP server on this Mac, moved off the
broken `:8080` config (8080 is held by OrbStack) onto a free port.

## TL;DR — what's running

- **Plugin HTTP server:** GhidraMCP (LaurieWired) plugin, bound to `127.0.0.1:9080`
  (plain HTTP, loopback only), token-authenticated.
- **MCP bridge:** `bridge_mcp_ghidra.py` (stdio MCP server) proxies Claude Code → `:9080`.
- **Chosen port:** **9080** (8080 = OrbStack; avoid 8829/43596/43597/37117).

## Architecture (both sides must agree on the port)

```
Claude Code  --stdio-->  bridge_mcp_ghidra.py  --HTTP + X-GhidraMCP-Token-->  GhidraMCP plugin (in Ghidra GUI) @ 127.0.0.1:9080
```

- The plugin is a Java plugin that runs an embedded HTTP server **inside a running
  Ghidra CodeBrowser tool**. Its port is a Ghidra **Tool Option**:
  `Edit → Tool Options → GhidraMCP HTTP Server → Server Port`.
- The bridge is a Python MCP server. It takes `--ghidra-server <URL>` (NOT `--base-port`)
  and `--token-file`. The port lives inside the URL.

## Key paths on this machine

| Thing | Path |
|---|---|
| Ghidra **install** (binaries, headless) | `/Users/robert/Applications/ghidra_12.0.4_PUBLIC/` |
| Ghidra **user settings** (preferences, tools, Extensions) | `/Users/robert/Library/ghidra/ghidra_12.0.4_PUBLIC/` |
| GUI launcher | `/Users/robert/Applications/ghidra_12.0.4_PUBLIC/ghidraRun` |
| Installed plugin extension | `~/Library/ghidra/ghidra_12.0.4_PUBLIC/Extensions/GhidraMCP/` (built for ghidraVersion 12.0.4) |
| Bridge script | `/Users/robert/.local/src/GhidraMCP/bridge_mcp_ghidra.py` |
| Token file | `/Users/robert/.config/ghidramcp/token` (plugin reads it; header `X-GhidraMCP-Token`) |
| **Persisted port option** | `~/Library/ghidra/ghidra_12.0.4_PUBLIC/tools/_code_browser.tcd` → `<CATEGORY NAME="GhidraMCP HTTP Server"><STATE NAME="Server Port" TYPE="int" VALUE="9080"/>` |
| Dedicated bridge venv | `/Users/robert/.local/share/ghidramcp/.venv-darkan/` (Python 3.12, `mcp==1.5.0`, `requests==2.32.3`) |

> Note: the port option is saved in the **default CodeBrowser tool template** in user
> settings, so *every* future CodeBrowser launch (including against the real
> `rs2client-948` project) binds 9080 automatically — no re-config needed.

## `.mcp.json` (final)

`/Users/robert/projects/darkan3-server/.mcp.json`:

```json
{
  "mcpServers": {
    "ghidra": {
      "command": "/Users/robert/.local/share/ghidramcp/.venv-darkan/bin/python",
      "args": [
        "/Users/robert/.local/src/GhidraMCP/bridge_mcp_ghidra.py",
        "--ghidra-server",
        "http://127.0.0.1:9080/",
        "--token-file",
        "/Users/robert/.config/ghidramcp/token"
      ]
    }
  }
}
```

(The old broken config pointed `command: python` at a non-existent Linux path
`/home/trent/.../bridge_mcp_ghidra.py` with bogus `--base-port 8080 --port-range 10`
flags that this bridge version doesn't even accept.)

## How it was set up (reproducible)

1. **Pick a free port:** `lsof -nP -iTCP:9080 -sTCP:LISTEN` → empty = free.
2. **Dedicated venv + deps:**
   ```sh
   uv venv --python 3.12 /Users/robert/.local/share/ghidramcp/.venv-darkan
   uv pip install --python /Users/robert/.local/share/ghidramcp/.venv-darkan/bin/python \
       -r /Users/robert/.local/src/GhidraMCP/requirements.txt   # mcp==1.5.0, requests==2.32.3
   ```
3. **Set the plugin port (GUI, one time):** launch `ghidraRun`, open/create any project,
   launch the **CodeBrowser** tool (green dragon in the Tool Chest), then
   `Edit → Tool Options → GhidraMCP HTTP Server → Server Port` = `9080`, Apply/OK.
   `File → Save Tool`, then `File → Close Tool` and relaunch the tool (or restart Ghidra)
   so the plugin re-reads the port — it only binds in its constructor at tool launch.
   (A throwaway project at `/tmp/mcp-port-scratch` was used for this so the locked
   real project was never touched.)
4. **Fix `.mcp.json`** as above.

## Verify it's up

```sh
TOK=$(cat /Users/robert/.config/ghidramcp/token)
# plugin listening + auth enforced:
curl -s -o /dev/null -w '%{http_code}\n' http://127.0.0.1:9080/methods            # 401 (no token)
curl -s -H "X-GhidraMCP-Token: $TOK" 'http://127.0.0.1:9080/methods?limit=5'       # 200 -> function list, or "No program loaded"
# which port the Ghidra GUI JVM is bound to:
lsof -nP -iTCP -sTCP:LISTEN -a -p "$(pgrep -f ghidra.GhidraRun | head -1)"          # -> 127.0.0.1:9080
# plugin log line:
grep "GhidraMCP HTTP server started" ~/Library/ghidra/ghidra_12.0.4_PUBLIC/application.log | tail -1
```

A full MCP round-trip (initialize → `tools/call list_methods`) through the bridge to
`:9080` was confirmed working; it returns `No program loaded` until a binary is open
in the CodeBrowser tool.

## Activating in Claude Code

`.mcp.json` is read at **client startup** — editing it does NOT hot-reload the current
session. To pick up the `ghidra` server:

- **Restart Claude Code**, or
- run **`/mcp`** and reconnect the `ghidra` server.

After that, the `ghidra` MCP tools (e.g. `list_methods`, `decompile_function`,
`run_java_class`) are available and proxy to the plugin on 9080.

## Project-lock coordination (IMPORTANT)

The accumulated RE lives in `/Users/robert/projects/reclass-data/rs2client-948.gpr`
(`.rep`). It is opened by a **headless RE batch** (`analyzeHeadless ... -readOnly
-postScript RS3DecompAt.java ...`) that runs back-to-back one-shot decomps and holds
`rs2client-948.lock` while each run is in flight.

- The MCP infra (above) was stood up against a **scratch** project, so it never touched
  the locked real project. The plugin is live on 9080 right now (serving the empty
  scratch program → tools return "No program loaded").
- **Deferred final step (do when the headless batch is idle):** open the real binary in
  a CodeBrowser tool so the MCP serves the real RE data:
  1. Confirm no headless run is active:
     `pgrep -fl "AnalyzeHeadless rs2client"` → empty, and
     `ls /Users/robert/projects/reclass-data/rs2client-948.lock` → absent.
  2. In Ghidra: `File → Open Project…` → `/Users/robert/projects/reclass-data/rs2client-948.gpr`.
     If prompted because it's locked, open **read-only** (read-only cannot corrupt the
     `.rep` and won't fight the headless `-readOnly` runs).
  3. Double-click `rs2client.948-5` to open it in CodeBrowser. The GhidraMCP plugin
     (already configured for 9080) serves that program; `list_methods` etc. now return
     real data.
- **Never delete a live `.lock`** while a `java`/headless process owns it — that risks
  corrupting the project. A stale `.lock` with no owning process is safe to remove only
  after verifying no Ghidra/headless process is running.

## Notes / cleanup

- Scratch project used only to set the port: `/tmp/mcp-port-scratch.{gpr,rep}` — safe to delete.
- Several older `bridge_mcp_ghidra.py` processes from other Claude sessions may still be
  running against the old `:8080` (from `~/.local/share/ghidramcp/.venv`). They're
  harmless; leave them unless you know the owning sessions are dead.
- Do not disturb: OrbStack (`:8080`), the game servers (`:8829`, `:43596`, `:43597`,
  mongo `:37117`), or the running headless RE batch.
