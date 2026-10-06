package com.ruoyi.ui.view.monitor.cache;


import cn.hutool.core.thread.ThreadUtil;
import com.muyun.vaadin.antd.button.Button;
import com.muyun.vaadin.antd.button.enums.ButtonType;
import com.muyun.vaadin.antd.card.Card;
import com.muyun.vaadin.antd.form.Form;
import com.muyun.vaadin.antd.form.enums.FormLayout;
import com.muyun.vaadin.antd.grid.Col;
import com.muyun.vaadin.antd.grid.Row;
import com.muyun.vaadin.antd.message.Message;
import com.muyun.vaadin.antd.procomponents.form.text.ProFormText;
import com.muyun.vaadin.antd.procomponents.form.text.ProFormTextArea;
import com.muyun.vaadin.antd.procomponents.table.ProTable;
import com.muyun.vaadin.antd.procomponents.table.props.ProColumn;
import com.ruoyi.system.domain.SysCache;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.ui.view.MainLayoutView;
import com.ruoyi.web.controller.monitor.CacheController;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存列表
 *
 * @author muyun
 */
@Route(value = "monitor/cacheList", layout = MainLayoutView.class)
@PermitAll
public class CacheListView extends Div {


    ProTable<SysCache> cacheListTable;


    ProTable<CacheKeyVo> cacheKeyListTable;

    ProFormText cacheNameField;

    ProFormText cacheKeyField;

    ProFormTextArea cacheValueField;


    transient CacheController cacheController;

    @Autowired
    public CacheListView(CacheController cacheController) {
        this.cacheController = cacheController;

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
        // 行： cup, 内存
        Row row1 = new Row(List.of(24, 24));
        {
            Col col1 = new Col(8);
            col1.add(createCacheListCard());
            //
            Col col2 = new Col(8);
            col2.add(createCacheKeyListCard());
            //
            Col col3 = new Col(8);
            col3.add(createCacheContentCard());
            //
            row1.add(col1, col2, col3);
        }
        //
        add(row1);
    }


    /**
     * @return
     */
    private Card createCacheListCard() {
        Card card = new Card("缓存名称(前缀)列表");
        {
            // columns
            ProColumn<SysCache> column1 = new ProColumn<SysCache>()
                    .setTitle("缓存名称(前缀)")
                    // .setDataIndex(SysCache::getCacheName)
                    .setRender(sysCache -> {
                        Button button = new Button(sysCache.getCacheName(), ButtonType.LINK)
                                .addClickListener(event -> {
                                    String cacheName = sysCache.getCacheName();
                                    cacheKeyListTable.setItems(getCacheKeyList(cacheName));
                                });
                        return button;
                    });

            //
            ProColumn<SysCache> column2 = new ProColumn<SysCache>()
                    .setTitle("备注")
                    .setDataIndex(SysCache::getRemark);

            //
            ProColumn<SysCache> column10 = new ProColumn<SysCache>()
                    .setTitle("操作")
                    .setRender(sysCache -> {
                        Button button = new Button("删除", ButtonType.LINK)
                                .setDanger(true)
                                .addClickListener(event -> {
                                    String cacheCacheName = sysCache.getCacheName();
                                    cacheController.clearCacheName(cacheCacheName);
                                    Message.success("清理缓存键名[" + cacheCacheName + "]成功");
                                });
                        return button;
                    });
            //
            cacheListTable = new ProTable<>(SysCache.class)
                    .setSearch(false)
                    .setColumns(List.of(column1, column2, column10))
                    .setRowKey(SysConfig.Fields.configName);
            //
            card.add(cacheListTable);
        }
        return card;
    }


    /**
     * @return
     */
    private Card createCacheKeyListCard() {
        Card card = new Card("键名列表");
        {
            // columns
            ProColumn<CacheKeyVo> column1 = new ProColumn<CacheKeyVo>()
                    .setTitle("序号")
                    .setDataIndex(CacheKeyVo::getIndex);
            //
            ProColumn<CacheKeyVo> column2 = new ProColumn<CacheKeyVo>()
                    .setTitle("缓存键名")
                    .setRender(cacheKeyVo -> {
                        Button button = new Button(cacheKeyVo.getCacheKey(), ButtonType.LINK)
                                .addClickListener(event -> {
                                    String cacheKey = cacheKeyVo.getCacheKey();
                                    cacheKeyField.setValue(cacheKey);
                                    // 从 cacheKey 中 获取 cacheName, : 前面的
                                    String[] splitArray = cacheKey.split(":");
                                    if (splitArray.length > 1) {
                                        String cacheName = cacheKey.split(":")[0];
                                        cacheNameField.setValue(cacheName);
                                    }
                                    //
                                    String cacheValue = cacheController.getCacheValue(cacheKey);
                                    cacheValueField.setValue(cacheValue);
                                });
                        return button;
                    });
            //
            ProColumn<CacheKeyVo> column10 = new ProColumn<CacheKeyVo>()
                    .setTitle("操作")
                    .setRender(sysCache1 -> {
                        Button button = new Button("删除", ButtonType.LINK)
                                .setDanger(true)
                                .addClickListener(event -> {
                                    String cacheKey = sysCache1.getCacheKey();
                                    cacheController.clearCacheKey(cacheKey);
                                    Message.success("清理key[" + cacheKey + "]成功");
                                    // 从 cacheKey 中 获取 cacheName, : 前面的
                                    String[] splitArray = cacheKey.split(":");
                                    if (splitArray.length > 1) {
                                        String cacheName = cacheKey.split(":")[0];
                                        cacheKeyListTable.setItems(getCacheKeyList(cacheName));
                                    }
                                });
                        return button;
                    });

            //
            cacheKeyListTable = new ProTable<>(CacheKeyVo.class)
                    .setSearch(false)
                    .setColumns(List.of(column1, column2, column10));
            //
            card.add(cacheKeyListTable);


        }
        return card;
    }


    /**
     * @return
     */
    private Card createCacheContentCard() {
        Card card = new Card("缓存内容");
        {
            Form form = new Form()
                    .setLayout(FormLayout.VERTICAL);
            {
                cacheNameField = new ProFormText()
                        .setLabel("缓存名称(前缀)");
                //
                cacheKeyField = new ProFormText()
                        .setLabel("缓存键名");
                //
                cacheValueField = new ProFormTextArea()
                        .setLabel("缓存内容");
                cacheValueField.getField().setRows(10);
                //
                form.add(cacheNameField, cacheKeyField, cacheValueField);
            }
            card.add(form);
        }
        return card;
    }


    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************

    void initBehavior() {
        //
        initRedisData();
    }


    void initRedisData() {
        UI ui = UI.getCurrent();
        ThreadUtil.execAsync(() -> {
            try {
                //
                ui.access(() -> {
                    //
                    cacheListTable.setItems(cacheController.getCaches());
                });
            } catch (Exception e) {
                e.printStackTrace();
                // todo 获取失败时的处理
            }
        });
    }


    /**
     * @param cacheName
     * @return
     */
    List<CacheKeyVo> getCacheKeyList(String cacheName) {
        List<String> cacheKeyList = cacheController.getCacheKeyList(cacheName);
        List<CacheKeyVo> cacheKeyVoList = new ArrayList<>();
        int index = 0;
        for (String key : cacheKeyList) {
            CacheKeyVo cacheKeyVo = new CacheKeyVo();
            cacheKeyVo.setIndex(index);
            cacheKeyVo.setCacheKey(key);
            cacheKeyVoList.add(cacheKeyVo);
            index++;
        }
        return cacheKeyVoList;
    }


    // *****************************************************************************************************************
    //                                    样式
    // *****************************************************************************************************************

    void initStyle() {
        // 样式
        this.getStyle().setPaddingLeft("12px").setPaddingRight("12px");
    }


    @Data
    @Accessors(chain = true)
    class CacheKeyVo {
        int index;
        String cacheKey;
    }

}
