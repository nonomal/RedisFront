package org.dromara.redisfront.ui.widget;

import cn.hutool.core.util.ArrayUtil;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import org.dromara.quickswing.events.QSEvent;
import org.dromara.quickswing.events.QSEventListener;
import org.dromara.quickswing.ui.swing.Background;
import org.dromara.redisfront.RedisFrontContext;
import org.dromara.redisfront.model.context.ConnectContext;
import org.dromara.redisfront.ui.components.NonePanel;
import org.dromara.redisfront.ui.components.extend.DrawerAnimationAction;
import org.dromara.redisfront.ui.event.OpenRedisConnectEvent;
import org.dromara.redisfront.ui.handler.OpenConnectHandler;
import org.dromara.redisfront.ui.widget.sidebar.MainSidebarComponent;
import org.dromara.redisfront.ui.widget.content.MainContentComponent;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Optional;


public class MainComponent extends Background {

    public static final int DEFAULT_DRAWER_WIDTH = 250;

    private final MainWidget owner;
    private final RedisFrontContext context;
    private JPanel mainLeftPanel;
    private JPanel mainRightPane;

    private MainContentComponent createMainTabbedPanel(DrawerAnimationAction drawerAnimationAction) {
        MainContentComponent mainRightTabbedPanel = new MainContentComponent(drawerAnimationAction, owner);
        mainRightTabbedPanel.setTabCloseProcess(count -> {
            if (count == 0) {
                if (!drawerAnimationAction.isDrawerOpen()) {
                    drawerAnimationAction.handleAction(null);
                }
                mainRightPane.removeAll();
                mainRightPane.add(NonePanel.getInstance(), BorderLayout.CENTER);
                FlatLaf.updateUI();
            }
        });
        return mainRightTabbedPanel;
    }

    public MainComponent(MainWidget owner) {
        this.owner = owner;
        this.context = (RedisFrontContext) owner.getContext();
        this.setLayout(new BorderLayout());
        this.initComponents();
    }

    private void initComponents() {
        var parentPanel = new JPanel(new BorderLayout());
        this.mainRightPane = new JPanel();
        this.mainRightPane.setLayout(new BorderLayout());
        this.mainRightPane.add(NonePanel.getInstance(), BorderLayout.CENTER);
        parentPanel.add(mainRightPane, BorderLayout.CENTER);

        var drawerAnimationAction = new DrawerAnimationAction(owner, (fraction, drawerOpen) -> {
            int width = getDrawerWidth(fraction, drawerOpen);
            this.mainLeftPanel.setPreferredSize(new Dimension(width, -1));
            this.mainLeftPanel.updateUI();
        });

        var openConnectProcessor = new OpenConnectHandler() {
            @Override
            public void accept(ConnectContext connectContext) {
                Component[] components = mainRightPane.getComponents();
                if (ArrayUtil.isNotEmpty(components)) {
                    Optional<Component> first = Arrays.stream(components).findFirst();
                    if (first.isPresent()) {
                        if (first.get() instanceof MainContentComponent mainRightTabbedPanel) {
                            //todo add tab
                            System.out.println("JTabbedPane " + first.get());
                        } else {
                            mainRightPane.removeAll();
                            MainContentComponent mainRightTabbedPanel = createMainTabbedPanel(drawerAnimationAction);
                            mainRightPane.add(mainRightTabbedPanel, BorderLayout.CENTER);
                            FlatLaf.updateUI();
                        }
                    }
                }
            }
        };

        context.getEventBus().subscribe(new QSEventListener<>() {
            @Override
            protected void onEvent(QSEvent qsEvent) {
                if (qsEvent instanceof OpenRedisConnectEvent openRedisConnectEvent) {
                    Object message = openRedisConnectEvent.getMessage();
                    openConnectProcessor.accept((ConnectContext) message);
                }
            }
        });

        this.mainLeftPanel = new MainSidebarComponent(owner, openConnectProcessor, drawerAnimationAction, (key, index) -> {
            System.out.println("drawerMenuItemEvent" + " key:" + key);
            System.out.println("drawerMenuItemEvent" + " index:" + Arrays.toString(index));
        }).buildPanel();
        this.mainLeftPanel.setMinimumSize(new Dimension(250, -1));
        this.mainLeftPanel.putClientProperty(FlatClientProperties.STYLE, "background:$RedisFront.main.background");
        parentPanel.add(mainLeftPanel, BorderLayout.WEST);

        this.add(parentPanel, BorderLayout.CENTER);
    }

    private static int getDrawerWidth(Double fraction, Boolean drawerOpen) {
        int width;
        if (drawerOpen) {
            width = (int) (DEFAULT_DRAWER_WIDTH - DEFAULT_DRAWER_WIDTH * fraction);
        } else {
            width = (int) (DEFAULT_DRAWER_WIDTH * fraction);
        }
        return width;
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (mainLeftPanel != null) {
            mainLeftPanel.updateUI();
        }
    }

}
