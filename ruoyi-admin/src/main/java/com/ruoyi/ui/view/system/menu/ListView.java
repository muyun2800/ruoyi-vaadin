package com.ruoyi.ui.view.system.menu;


import cn.hutool.core.bean.BeanUtil;
import com.muyun.vaadin.antd.button.Button;
import com.muyun.vaadin.antd.button.enums.ButtonType;
import com.muyun.vaadin.antd.flex.Flex;
import com.muyun.vaadin.antd.icon.Icon;
import com.muyun.vaadin.antd.icon.enums.IconName;
import com.muyun.vaadin.antd.notification.Notification;
import com.muyun.vaadin.antd.procomponents.common.render.AnchorGroupRender;
import com.muyun.vaadin.antd.procomponents.common.render.TagRender;
import com.muyun.vaadin.antd.procomponents.field.ProFieldValueType;
import com.muyun.vaadin.antd.procomponents.table.ProTable;
import com.muyun.vaadin.antd.procomponents.table.data.FetchCallback;
import com.muyun.vaadin.antd.procomponents.table.data.FetchResult;
import com.muyun.vaadin.antd.procomponents.table.data.FilterMap;
import com.muyun.vaadin.antd.procomponents.table.props.OptionConfig;
import com.muyun.vaadin.antd.procomponents.table.props.ProColumn;
import com.muyun.vaadin.antd.procomponents.table.props.RowEditableConfig;
import com.muyun.vaadin.antd.procomponents.table.props.TablePaginationConfig;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.ui.view.MainLayoutView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.ruoyi.common.utils.SecurityUtils.getUserId;


/**
 * @author muyun
 */
@Route(value = "system/menu", layout = MainLayoutView.class)
@PermitAll
@Slf4j
public class ListView extends Div {

    transient ISysDictTypeService dictTypeService;

    transient ISysMenuService menuService;

    @Autowired
    public ListView(ISysDictTypeService dictTypeService, ISysMenuService menuService) {
        this.dictTypeService = dictTypeService;
        this.menuService = menuService;

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

    ProTable<SysMenu> proTable;

    EditModal editModal;


    void initUI() {
        proTable = new ProTable<>(SysMenu.class)
                .setColumns(createColumns())
                .setCardBordered(true)
                .setDateFormatter("string")
                .setHeaderTitle("查询表格")
                .setRowKey(SysMenu.Fields.menuId)
                .setEditable(new RowEditableConfig()
                        .setType("multiple"))
                .setOptions(getOptionConfig())
                .setPagination(new TablePaginationConfig().setDefaultPageSize(15))
                .setToolBarRender(createToolBarRender());
        // 配置行选择功能
        proTable.getRowSelection().setEnabled(true);
        // 创建编辑模态框
        editModal = new EditModal(proTable, menuService, dictTypeService);

        //
        add(proTable, editModal);
    }


    /**
     * 创建 toolBarRender
     *
     * @return
     */
    ProTable.ToolBarRender createToolBarRender() {
        return (action, selectedRowKeys, selectedRows) -> {
            Flex container = new Flex().setGap("8px");
            {
                Button insertButton = new Button("新增", ButtonType.PRIMARY)
                        .setIcon(Icon.of(IconName.PLUS_OUTLINED))
                        .addClickListener(clickEvent -> handleInsertEvent());
                //
                container.add(insertButton);
            }
            return container;
        };
    }


    /**
     * @return
     */
    List<ProColumn<SysMenu>> createColumns() {
        //
        ProColumn<SysMenu> column1 = new ProColumn<SysMenu>()
                .setTitle("菜单名称")
                .setDataIndex(SysMenu::getMenuName);
        //
        ProColumn<SysMenu> column2 = new ProColumn<SysMenu>()
                .setTitle("菜单图标")
                .setDataIndex(SysMenu::getIcon)
                .setHideInSearch(true);
        //
        ProColumn<SysMenu> column3 = new ProColumn<SysMenu>()
                .setTitle("显示顺序")
                .setDataIndex(SysMenu::getOrderNum)
                .setHideInSearch(true);
        //
        ProColumn<SysMenu> column4 = new ProColumn<SysMenu>()
                .setTitle("组件路径")
                .setDataIndex(SysMenu::getPath)
                .setHideInSearch(true);
        //
        ProColumn<SysMenu> column5 = new ProColumn<SysMenu>()
                .setTitle("权限标识")
                .setDataIndex(SysMenu::getPerms)
                .setHideInSearch(true);
        //
        ProColumn<SysMenu> column6 = new ProColumn<SysMenu>()
                .setTitle("菜单状态")
                .setDataIndex(SysMenu::getStatus)
                .setValueEnum(dictTypeService.getValueEnum("sys_normal_disable"))
                .setRender(new TagRender<>(SysMenu::getStatus));
        //
        ProColumn<SysMenu> column10 = new ProColumn<SysMenu>()
                .setTitle("操作")
                .setValueType(ProFieldValueType.OPTION)
                .setHideInSearch(true)
                .setWidth("300px")
                .setRender(new AnchorGroupRender<SysMenu>()
                        .add("修改", this::handleUpdateEvent)
                        .addDeleteAnchor(this::handleDeleteEvent)
                );


        return List.of(column1, column2, column3, column4, column5, column6, column10);
    }


    /**
     * @return
     */
    private OptionConfig getOptionConfig() {
        return new OptionConfig()
                .setSetting(new OptionConfig.SettingOptionType()
                        .setListsHeight(400));
    }

    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************


    void initBehavior() {
        // 获取数据
        FetchCallback<SysMenu> fetchCallback = fetchParam -> {
            FilterMap filter = fetchParam.getFilter();
            SysMenu queryObj = new SysMenu();
            BeanUtil.copyProperties(filter, queryObj);
            //
            List<SysMenu> resultList = menuService.selectMenuList(queryObj, getUserId());
            // resultList 转为树形结构
            List<SysMenu> resultList2 = menuService.buildMenuTree(resultList);
            recursiveCleanEmptyChildren(resultList2);
            // 转为结果
            return new FetchResult<SysMenu>()
                    .setTotal(PageUtils.getTotal(resultList2))
                    .setRecords(resultList2);
        };
        //
        proTable.setItems(fetchCallback);
    }


    /**
     * 适配proTable树表，无子节点，children要为null
     *
     * @param list
     */
    void recursiveCleanEmptyChildren(List<SysMenu> list) {
        for (SysMenu sysDept : list) {
            if (sysDept.getChildren().isEmpty()) {
                sysDept.setChildren(null);
            } else {
                recursiveCleanEmptyChildren(sysDept.getChildren());
            }
        }
    }


    /**
     * 处理新增事件
     */
    void handleInsertEvent() {
        editModal.openInsertModal();
    }


    /**
     * 处理修改事件
     *
     * @param dataItem
     */
    void handleUpdateEvent(SysMenu dataItem) {
        editModal.openUpdateModal(dataItem.getMenuId());
    }


    /**
     * 处理删除事件
     *
     * @param dataItem
     */
    void handleDeleteEvent(SysMenu dataItem) {
        try {
            menuService.deleteMenuById(dataItem.getMenuId());
            Notification.success("删除成功");
            proTable.getActionRef().reload();
        } catch (Exception e) {
            log.error("删除失败", e);
            Notification.error("删除失败");
        }
    }


    // *****************************************************************************************************************
    //                                    样式
    // *****************************************************************************************************************

    void initStyle() {
        // 样式

    }


}

