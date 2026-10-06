package com.ruoyi.ui.view;


import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.muyun.vaadin.antd.dropdown.Dropdown;
import com.muyun.vaadin.antd.dropdown.props.MenuProps;
import com.muyun.vaadin.antd.menu.props.MenuItem;
import com.muyun.vaadin.antd.procomponents.layout.ProLayoutWithNavTabs;
import com.muyun.vaadin.antd.procomponents.layout.enums.MenuLayout;
import com.muyun.vaadin.antd.procomponents.layout.props.AvatarProps;
import com.muyun.vaadin.antd.procomponents.layout.props.BgLayoutImg;
import com.muyun.vaadin.antd.procomponents.layout.props.RouteItem;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.framework.security.vaadin.VaadinSecurityService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.ui.view.base.auth.AccountSettingsModal;
import com.ruoyi.ui.view.base.auth.UpdatePasswordModal;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * @author muyun
 */
@Route("index")
@PermitAll
@Slf4j
@SuppressWarnings("java:S110")
public class MainLayoutView extends ProLayoutWithNavTabs implements BeforeEnterObserver {


    public static final String PATH_PREFIX = "/";

    @Autowired
    transient VaadinSecurityService vaadinSecurityService;


    transient RouteItem routeItem;


    transient ISysMenuService menuService;


    transient ISysUserService userService;

    transient ISysDictTypeService dictTypeService;

    transient TokenService tokenService;


    UpdatePasswordModal updatePasswordModal;


    AccountSettingsModal accountSettingsModal;


    public MainLayoutView(ISysMenuService menuService, ISysUserService userService, ISysDictTypeService dictTypeService,
                          TokenService tokenService) {
        this.menuService = menuService;
        this.userService = userService;
        this.dictTypeService = dictTypeService;
        this.tokenService = tokenService;

        // 初始化 UI
        initUI();

        // 初始化行为
        initBehavior();

        // 初始化样式
        initStyle();


    }

    // *****************************************************************************************************************
    //                                      结构
    // *****************************************************************************************************************

    void initUI() {
        this.setLayout(MenuLayout.MIX);
        this.setTitle("Ant Design Pro");
        this.setLogo("https://gw.alipayobjects.com/zos/rmsportal/KDpgvguMpGfqaHPjicRK.svg");
        this.setFixSiderbar(true);
        // BgLayoutImgList
        {
            BgLayoutImg img1 = new BgLayoutImg().setLeft(85).setBottom(100).setHeight("303px")
                                                .setSrc("https://img.alicdn.com/imgextra/i2/O1CN01O4etvp1DvpFLKfuWq_!!6000000000279-2-tps-609-606.png");
            BgLayoutImg img2 = new BgLayoutImg().setRight(-45).setBottom(-68).setHeight("303px")
                                                .setSrc("https://img.alicdn.com/imgextra/i2/O1CN01O4etvp1DvpFLKfuWq_!!6000000000279-2-tps-609-606.png");
            BgLayoutImg img3 = new BgLayoutImg().setLeft(0).setBottom(0).setHeight("331x")
                                                .setSrc("https://img.alicdn.com/imgextra/i3/O1CN018NxReL1shX85Yz6Cx_!!6000000005798-2-tps-884-496.png");
            this.setBgLayoutImgList(List.of(img1, img2, img3));
        }
        //
        // 模态框
        updatePasswordModal = new UpdatePasswordModal(userService);
        accountSettingsModal = new AccountSettingsModal(userService, dictTypeService, tokenService);
        //
        add(updatePasswordModal, accountSettingsModal);


    }


    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String path = PATH_PREFIX + event.getLocation().getPath();
        String tabName = getTabName(event);
        // 根据 route path, 查找 匹配的View(有@Route注解的)
        Optional<Class<? extends Component>> viewOptional = RouteConfiguration.forApplicationScope().getRoute(event.getLocation().getPath());
        if (viewOptional.isPresent()) {
            // 判断 view上是否有 @PageTitle注解, 使用反射
            PageTitle pageTitleObj = AnnotationUtil.getAnnotation(viewOptional.get(), PageTitle.class);
            if (pageTitleObj != null) {
                tabName = pageTitleObj.value();
            }
        }
        // 如果tabName还是为null，则使用path
        if (tabName == null) {
            tabName = event.getLocation().getPath();
        }
        // 打开导航标签
        openNavTab(path, tabName, true);
    }


    /**
     * @param event
     * @return
     */
    private String getTabName(BeforeEnterEvent event) {
        // 路径的各个部分，例如:[system ,log, operlog]
        List<String> pathSegments = event.getLocation().getSegments();
        // 遍历 routeItem的子项，获取匹配路由名称。正常情况下，最后一个pathSegment匹配到的routeItemTemp就是我们要找的
        RouteItem routeItemTemp = routeItem;
        for (String pathSegment : pathSegments) {
            if (routeItemTemp == null) {
                break;
            }
            routeItemTemp = getRouteItemByRelativePath(routeItemTemp.getChildren(), pathSegment);
        }
        return routeItemTemp != null ? routeItemTemp.getName() : null;
    }


    /**
     * 根据相对路径获取RouteItem
     *
     * @param routeItemChildren
     * @param path              相对路径
     * @return
     */
    private RouteItem getRouteItemByRelativePath(List<RouteItem> routeItemChildren, String path) {
        if (CollUtil.isEmpty(routeItemChildren)) {
            return null;
        }
        for (RouteItem item : routeItemChildren) {
            if (CharSequenceUtil.equals(item.getPath(), path)) {
                return item;
            }
        }
        return null;
    }


    void initBehavior() {
        // 加载用户头像
        initUserAvatar();
        // 加载菜单数据
        Long userId = SecurityUtils.getUserId();
        routeItem = getRouteItem(userId);
        this.setRoute(routeItem);
    }


    /**
     * 用户头像加载
     */
    void initUserAvatar() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getUser();
        String avatarImage = CharSequenceUtil.isNotEmpty(sysUser.getAvatar()) ? sysUser.getAvatar() : "https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png";
        AvatarProps avatarProps = new AvatarProps()
                .setSrc(avatarImage)
                // .setSize("small")
                .setTitle(sysUser.getNickName())
                .setRender((title, src, size) -> {
                    Component avatarContainer = getDefaultAvatarComponent(title, src, size);
                    // 添加下拉菜单
                    Dropdown dropdown = new Dropdown();
                    MenuProps menuProps = createMenuProps();
                    dropdown.setMenu(menuProps);
                    dropdown.add(avatarContainer);
                    return dropdown;
                });

        super.setAvatarProps(avatarProps);

    }


    MenuProps createMenuProps() {
        MenuItem menuItem1 = new MenuItem("1", "个人设置");
        MenuItem menuItem2 = new MenuItem("2", "修改密码");
        MenuItem menuItem10 = new MenuItem("10", "退出登录");

        //
        List<MenuItem> items = ListUtil.of(menuItem1, menuItem2, menuItem10);
        MenuProps menuProps = new MenuProps(items);
        menuProps.addClickListener(event -> {
            if (ObjectUtil.equal(event.getKey(), "1")) {
                accountSettingsModal.openUpdateModal();
            } else if (ObjectUtil.equal(event.getKey(), "2")) {
                updatePasswordModal.openModal();
            } else if (ObjectUtil.equal(event.getKey(), "10")) {
                vaadinSecurityService.logout();
            }
        });
        return menuProps;
    }


    //
    public RouteItem getRouteItem(Long userId) {
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        RouteItem rootItem = new RouteItem();
        rootItem.setPath("/");
        buildRouteItem(menus, rootItem);
        return rootItem;
    }

    private void buildRouteItem(List<SysMenu> menus, RouteItem parentItem) {
        for (SysMenu menu : menus) {
            RouteItem item1 = new RouteItem();
            item1.setPath(menu.getPath());
            item1.setName(menu.getMenuName());
            item1.setIcon(menu.getIcon());
            //
            parentItem.add(item1);
            // 递归, 处理子想
            List<SysMenu> menuChildren = menu.getChildren();
            buildRouteItem(menuChildren, item1);
        }
    }


    // *****************************************************************************************************************
    //                                    样式
    // *****************************************************************************************************************

    void initStyle() {
        // 样式

    }
}
