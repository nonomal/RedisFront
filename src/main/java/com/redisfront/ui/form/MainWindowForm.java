package com.redisfront.ui.form;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatTabbedPaneCloseIcon;
import com.redisfront.constant.UI;
import com.redisfront.model.ConnectInfo;
import com.redisfront.service.ConnectService;
import com.redisfront.service.RedisService;
import com.redisfront.ui.component.MainTabbedPanel;
import com.redisfront.ui.dialog.AddConnectDialog;
import com.redisfront.ui.dialog.OpenConnectDialog;
import com.redisfront.util.Fn;
import com.redisfront.util.MsgUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

/**
 * MainWindowForm
 *
 * @author Jin
 */
public class MainWindowForm {
    private JPanel contentPanel;
    private JTabbedPane tabPanel;
    private static MainWindowForm mainWindowForm;

    public static MainWindowForm getInstance() {
        if (Fn.isNull(mainWindowForm)) {
            mainWindowForm = new MainWindowForm();
        }
        return mainWindowForm;
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public MainWindowForm() {
        $$$setupUI$$$();
        contentPanel.add(MainNoneForm.getInstance().getContentPanel(), BorderLayout.CENTER);
    }

    public void addActionPerformed(ConnectInfo connectInfo) {
        //存数据库
        if (Fn.equal(connectInfo.id(), 0)) {
            ConnectService.service.save(connectInfo);
        } else {
            ConnectService.service.update(connectInfo);
        }

        //set redis mode
        connectInfo.setRedisModeEnum(RedisService.service.getRedisModeEnum(connectInfo));

        var mainTabbedPanel = MainTabbedPanel.newInstance(connectInfo);

        //添加到tab面板
        this.tabPanel.addTab(connectInfo.title(), UI.MAIN_TAB_DATABASE_ICON, mainTabbedPanel);
        this.tabPanel.setSelectedIndex(tabPanel.getTabCount() - 1);
        this.contentPanel.add(tabPanel, BorderLayout.CENTER, 0);


    }

    private void createUIComponents() {
        UIManager.put("TabbedPane.closeHoverForeground", Color.red);
        UIManager.put("TabbedPane.selectHighlight", Color.red);
        UIManager.put("*.selectionBackground", Color.red);
        UIManager.put("TabbedPane.closePressedForeground", Color.red);
        UIManager.put("TabbedPane.closeHoverBackground", new Color(0, true));
        UIManager.put("TabbedPane.closeIcon", new FlatTabbedPaneCloseIcon());
        tabPanel = new JTabbedPane();
        tabPanel.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_ICON_PLACEMENT, SwingConstants.LEFT);
        //SHOW LINE
        tabPanel.putClientProperty(FlatClientProperties.TABBED_PANE_SHOW_TAB_SEPARATORS, true);
        //SHOW CLOSE BUTTON
        tabPanel.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSABLE, true);

        tabPanel.putClientProperty(FlatClientProperties.TABBED_PANE_SCROLL_BUTTONS_PLACEMENT, FlatClientProperties.TABBED_PANE_PLACEMENT_BOTH);
        //SHOW CLOSE BUTTON
        tabPanel.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "关闭连接");
        //SHOW CLOSE BUTTON Callback
        tabPanel.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_CALLBACK, (BiConsumer<JTabbedPane, Integer>) (tabbedPane, tabIndex) -> {
            Component component = tabbedPane.getComponentAt(tabIndex);
            if (component instanceof MainTabbedPanel mainTabbedPanel) {
                mainTabbedPanel.shutdownScheduled();
            }
            tabbedPane.removeTabAt(tabIndex);
            if (tabbedPane.getTabCount() == 0) {
                contentPanel.add(MainNoneForm.getInstance().getContentPanel(), BorderLayout.CENTER, 0);
            }
        });

        tabPanel.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_ALIGNMENT, SwingConstants.LEADING);
        tabPanel.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_AREA_ALIGNMENT, FlatClientProperties.TABBED_PANE_ALIGN_LEADING);
        tabPanel.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_TYPE, FlatClientProperties.TABBED_PANE_TAB_TYPE_UNDERLINED);

        var toolBar = new JToolBar();
        toolBar.setBorder(new EmptyBorder(10, 10, 10, 10));
        toolBar.setLayout(new BorderLayout());
        var jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout());

        var newBtn = new JButton(null, UI.NEW_CONN_ICON);
        newBtn.setToolTipText("新建连接");
        newBtn.addActionListener(e -> AddConnectDialog.showAddConnectDialog(
                //打开连接回调
                (this::addActionPerformed)
        ));
        jPanel.add(newBtn);

        var openBtn = new JButton(null, UI.OPEN_CONN_ICON);
        openBtn.setToolTipText("打开连接");
        openBtn.addActionListener(e -> OpenConnectDialog.showOpenConnectDialog(
                //打开连接回调
                (this::addActionPerformed),
                //编辑连接回调
                (connectInfo -> AddConnectDialog.showEditConnectDialog(connectInfo, (this::addActionPerformed))),
                //删除连接回调
                (connectInfo -> ConnectService.service.delete(connectInfo.id()))));
        jPanel.add(openBtn);
        toolBar.add(jPanel, BorderLayout.SOUTH);
        tabPanel.putClientProperty(FlatClientProperties.TABBED_PANE_TRAILING_COMPONENT, toolBar);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(0, 0));
        contentPanel.setEnabled(true);
        tabPanel.setTabLayoutPolicy(1);
        tabPanel.setTabPlacement(2);
        contentPanel.add(tabPanel, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPanel;
    }

}
