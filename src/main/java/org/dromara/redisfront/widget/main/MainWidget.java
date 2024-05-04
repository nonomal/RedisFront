package org.dromara.redisfront.widget.main;


import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.SystemInfo;
import org.dromara.quickswing.ui.app.AppContext;
import org.dromara.quickswing.ui.app.AppWidget;
import org.dromara.redisfront.RedisFrontPrefs;
import org.dromara.redisfront.commons.constant.UI;
import javax.swing.*;
import java.awt.*;

public class MainWidget extends AppWidget<RedisFrontPrefs> {
    public MainWidget(AppContext<? extends AppWidget<RedisFrontPrefs>, RedisFrontPrefs> context, String title, RedisFrontPrefs prefs) throws HeadlessException {
        super(context, title, prefs);
        if (SystemInfo.isWindows) {
            FlatLaf.setUseNativeWindowDecorations(true);
            this.rootPane.putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
        }
        if (SystemInfo.isMacFullWindowContentSupported) {
            this.rootPane.putClientProperty("apple.awt.fullWindowContent", true);
            this.rootPane.putClientProperty("apple.awt.transparentTitleBar", true);
            this.rootPane.putClientProperty("apple.awt.windowTitleVisible", false);
            this.rootPane.putClientProperty( FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING,
                    FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING_MEDIUM );
        }
        this.setResizable(true);
        this.setSize(960, 600);
        this.setIconImages(UI.MAIN_FRAME_ICON_IMAGES);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(new MainComponent(this));
    }
    private static boolean isMacOSMaximized(JFrame frame) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        DisplayMode displayMode = device.getDisplayMode();
        Dimension screenSize = new Dimension(displayMode.getWidth(), displayMode.getHeight());

        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());
        int menuBarHeight = screenInsets.top; // 菜单栏的高度
        int dockHeight = screenInsets.bottom; // Dock 的高度

        // 获取屏幕工作区域大小，去除菜单栏和 Dock 的高度
        int screenWidth = screenSize.width - screenInsets.left - screenInsets.right;
        int screenHeight = screenSize.height - screenInsets.top - screenInsets.bottom;

        return frame.getWidth() == screenWidth && frame.getHeight() == screenHeight - menuBarHeight - dockHeight;
    }
    @Override
    protected void preMenuBarInit(RedisFrontPrefs redisFrontPrefs, SplashScreen splashScreen) {

    }
}
