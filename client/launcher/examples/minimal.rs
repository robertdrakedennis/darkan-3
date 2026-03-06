use tao::event::{Event, WindowEvent};
use tao::event_loop::{ControlFlow, EventLoop};
use tao::platform::unix::WindowExtUnix;
use tao::window::WindowBuilder;
use wry::WebViewBuilder;
use wry::WebViewBuilderExtUnix;

fn main() {
    let event_loop = EventLoop::new();
    let window = WindowBuilder::new()
        .with_title("Test")
        .with_inner_size(tao::dpi::LogicalSize::new(400.0, 300.0))
        .build(&event_loop)
        .unwrap();

    let vbox = window.default_vbox().unwrap();
    let _webview = WebViewBuilder::new()
        .with_html("<html><body><h1 style='color:white;background:#1a1a2e;padding:40px;margin:0;height:100vh'>Hello from wry!</h1></body></html>")
        .build_gtk(vbox)
        .unwrap();

    event_loop.run(move |event, _, control_flow| {
        *control_flow = ControlFlow::Wait;
        if let Event::WindowEvent {
            event: WindowEvent::CloseRequested,
            ..
        } = event
        {
            *control_flow = ControlFlow::Exit;
        }
    });
}
