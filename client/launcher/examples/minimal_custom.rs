use tao::event::{Event, WindowEvent};
use tao::event_loop::{ControlFlow, EventLoopBuilder};
use tao::platform::unix::WindowExtUnix;
use tao::window::WindowBuilder;
use wry::WebViewBuilder;
use wry::WebViewBuilderExtUnix;

#[derive(Debug)]
enum UserEvent {
    Test(String),
}

fn main() {
    let event_loop = EventLoopBuilder::<UserEvent>::with_user_event().build();
    let proxy = event_loop.create_proxy();

    let window = WindowBuilder::new()
        .with_title("Test Custom Events")
        .with_inner_size(tao::dpi::LogicalSize::new(520.0, 480.0))
        .build(&event_loop)
        .unwrap();

    let vbox = window.default_vbox().unwrap();

    let _webview = WebViewBuilder::new()
        .with_html(r#"<html><body style="background:#1a1a2e;color:white;padding:40px;font-family:sans-serif">
            <h1>Darkan Launcher</h1>
            <p>If you can see this, the webview works!</p>
            <button onclick="window.ipc.postMessage('hello')">Test IPC</button>
            <div id="log"></div>
            <script>
                window.__darkan_callback = function(data) {
                    document.getElementById('log').innerHTML += '<p>' + JSON.stringify(data) + '</p>';
                };
            </script>
        </body></html>"#)
        .with_ipc_handler(move |msg| {
            println!("IPC received: {}", msg.body());
            let _ = proxy.send_event(UserEvent::Test(msg.body().to_string()));
        })
        .build_gtk(vbox)
        .unwrap();

    event_loop.run(move |event, _, control_flow| {
        *control_flow = ControlFlow::Wait;
        match event {
            Event::WindowEvent {
                event: WindowEvent::CloseRequested,
                ..
            } => *control_flow = ControlFlow::Exit,
            Event::UserEvent(UserEvent::Test(msg)) => {
                println!("UserEvent received: {}", msg);
                let _ = _webview.evaluate_script("window.__darkan_callback({type:'reply', msg:'hello from rust'})");
            }
            _ => {}
        }
    });
}
