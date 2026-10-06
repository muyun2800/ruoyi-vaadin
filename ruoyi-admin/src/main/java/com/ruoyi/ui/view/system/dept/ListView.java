package com.ruoyi.ui.view.system.dept;


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
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.ui.view.MainLayoutView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * @author muyun
 */
@Route(value = "system/dept", layout = MainLayoutView.class)
@PermitAll
@Slf4j
public class ListView extends Div {

    transient ISysDictTypeService dictTypeService;

    transient ISysDeptService deptService;



    @Autowired
    public ListView(ISysDictTypeService dictTypeService, ISysDeptService deptService) {
        this.dictTypeService = dictTypeService;
        this.deptService = deptService;

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

    ProTable<SysDept> proTable;

    EditModal editModal;


    void initUI() {
        proTable = new ProTable<>(SysDept.class)
                .setColumns(createColumns())
                .setCardBordered(true)
                .setDateFormatter("string")
                .setHeaderTitle("查询表格")
                .setRowKey(SysDept.Fields.deptId)
                .setEditable(new RowEditableConfig()
                        .setType("multiple"))
                .setOptions(getOptionConfig())
                .setPagination(new TablePaginationConfig().setDefaultPageSize(15))
                .setToolBarRender(createToolBarRender());
        // 配置行选择功能
        proTable.getRowSelection().setEnabled(true);
        // 创建编辑模态框
        editModal = new EditModal(proTable, deptService, dictTypeService);

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
    List<ProColumn<SysDept>> createColumns() {

        //
        ProColumn<SysDept> column1 = new ProColumn<SysDept>()
                .setTitle("部门名称")
                .setDataIndex(SysDept::getDeptName);
        //
        ProColumn<SysDept> column2 = new ProColumn<SysDept>()
                .setTitle("显示顺序")
                .setHideInSearch(true)
                .setDataIndex(SysDept::getOrderNum);
        //

        ProColumn<SysDept> column3 = new ProColumn<SysDept>()
                .setTitle("部门状态")
                .setDataIndex(SysDept::getStatus)
                .setValueEnum(dictTypeService.getValueEnum("sys_normal_disable"))
                .setRender(new TagRender<>(SysDept::getStatus));

        //
        ProColumn<SysDept> column4 = new ProColumn<SysDept>()
                .setTitle("备注")
                .setDataIndex(SysDept::getRemark)
                .setHideInSearch(true)
                .setEllipsis(true)
                .setWidth("400px");

        //
        //
        ProColumn<SysDept> column10 = new ProColumn<SysDept>()
                .setTitle("操作")
                .setValueType(ProFieldValueType.OPTION)
                .setHideInSearch(true)
                .setWidth("300px")
                .setRender(new AnchorGroupRender<SysDept>()
                        .add("修改", this::handleUpdateEvent)
                        .addDeleteAnchor(this::handleDeleteEvent)
                );


        return List.of(column1, column2, column3, column4, column10);
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
        FetchCallback<SysDept> fetchCallback = fetchParam -> {
            FilterMap filter = fetchParam.getFilter();
            SysDept queryObj = new SysDept();
            BeanUtil.copyProperties(filter, queryObj);
            //
            List<SysDept> resultList = deptService.selectDeptList(queryObj);
            // resultList 转为树形结构
            List<SysDept>  resultList2 = deptService.buildDeptTree(resultList);
            recursiveCleanEmptyChildren(resultList2);
            // 转为结果
            return new FetchResult<SysDept>()
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
    void recursiveCleanEmptyChildren(List<SysDept> list) {
        for (SysDept sysDept : list) {
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
    void handleUpdateEvent(SysDept dataItem) {
        editModal.openUpdateModal(dataItem.getDeptId());
    }


    /**
     * 处理删除事件
     *
     * @param dataItem
     */
    void handleDeleteEvent(SysDept dataItem) {
        try {
            deptService.deleteDeptById(dataItem.getDeptId());
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

