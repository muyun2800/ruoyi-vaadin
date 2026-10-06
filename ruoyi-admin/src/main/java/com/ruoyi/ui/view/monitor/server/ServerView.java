package com.ruoyi.ui.view.monitor.server;


import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.muyun.vaadin.antd.card.Card;
import com.muyun.vaadin.antd.grid.Col;
import com.muyun.vaadin.antd.grid.Row;
import com.muyun.vaadin.antd.table.Table;
import com.muyun.vaadin.antd.table.props.TableColumn;
import com.ruoyi.framework.web.domain.Server;
import com.ruoyi.framework.web.domain.server.*;
import com.ruoyi.ui.view.MainLayoutView;
import com.ruoyi.ui.vo.ServerVo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务监控
 *
 * @author muyun
 */
@Route(value = "monitor/server", layout = MainLayoutView.class)
@PermitAll
public class ServerView extends Div {


    Table tblCpu;
    Table tblMem;

    Table tblSys;

    Table tblJvm;

    Table tblDisk;


    public ServerView() {

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
        Row row1 = new Row(List.of(24, 16));
        {
            //  cup
            Col col11 = new Col(12)
                    .add(createCpuCard());
            // 内存
            Col col12 = new Col(12)
                    .add(createMemCard());
            //  服务器信息
            Col col21 = new Col(24)
                    .add(createSysCard());
            // 服务器信息
            Col col31 = new Col(24)
                    .add(createJvmCard());
            //  磁盘信息
            Col col41 = new Col(24)
                    .add(createDiskCard());
            //
            row1.add(col11, col12, col21, col31, col41);
        }
        //
        add(row1);
    }

    /**
     * @return
     */
    private Card createCpuCard() {
        Card card = new Card("CPU");
        {
            List<TableColumn> columns = ListUtil.of(
                    new TableColumn("属性", "name", "name"),
                    new TableColumn("值", "value", "value")
            );
            tblCpu = new Table()
                    .setRowKey("name")
                    .setPagination(false)
                    .setShowHeader(false)
                    .setColumns(columns)
                    .setLoading(true);

            //
            card.add(tblCpu);
        }
        return card;
    }


    private Card createMemCard() {
        Card card = new Card("内存");
        {
            List<TableColumn> columns = ListUtil.of(
                    new TableColumn("属性", "name", "name"),
                    new TableColumn("内存", "mem", "mem"),
                    new TableColumn("JVM", "jvm", "jvm")
            );
            tblMem = new Table()
                    .setRowKey("name")
                    .setPagination(false)
                    .setShowHeader(false)
                    .setColumns(columns)
                    .setLoading(true);
            //
            card.add(tblMem);
        }
        return card;
    }

    /**
     * @return
     */
    private Card createSysCard() {
        Card card = new Card("服务器系统信息");
        {
            List<TableColumn> columns = ListUtil.of(
                    new TableColumn("col1", "col1", "col1"),
                    new TableColumn("col2", "col2", "col2"),
                    new TableColumn("col3", "col3", "col3"),
                    new TableColumn("col4", "col4", "col4")
            );
            tblSys = new Table()
                    .setRowKey("name")
                    .setPagination(false)
                    .setShowHeader(false)
                    .setColumns(columns)
                    .setLoading(true);
            //
            card.add(tblSys);
        }
        return card;
    }

    /**
     * @return
     */
    private Card createJvmCard() {
        Card card = new Card("Java虚拟机信息");
        {
            List<TableColumn> columns = ListUtil.of(
                    new TableColumn("col1", "col1", "col1"),
                    new TableColumn("col2", "col2", "col2"),
                    new TableColumn("col3", "col3", "col3"),
                    new TableColumn("col4", "col4", "col4")
            );
            tblJvm = new Table()
                    .setRowKey("name")
                    .setPagination(false)
                    .setShowHeader(false)
                    .setColumns(columns)
                    .setLoading(true);
            //
            card.add(tblJvm);
        }
        return card;
    }


    /**
     * @return
     */
    private Card createDiskCard() {
        Card card = new Card("磁盘状态");
        {
            List<TableColumn> columns = ListUtil.of(
                    new TableColumn("盘符路径", "dirName", "dirName"),
                    new TableColumn("文件系统", "sysTypeName", "sysTypeName"),
                    new TableColumn("盘符类型", "typeName", "typeName"),
                    new TableColumn("总大小", "total", "total"),
                    new TableColumn("可用大小", "free", "free"),
                    new TableColumn("已用大小", "used", "used"),
                    new TableColumn("已用百分比", "usage", "usage")
            );
            tblDisk = new Table()
                    .setRowKey("name")
                    .setPagination(false)
                    .setColumns(columns)
                    .setLoading(true);
            //
            card.add(tblDisk);
        }
        return card;
    }


    // *****************************************************************************************************************
    //                                     行为
    // *****************************************************************************************************************

    void initBehavior() {
        //

        //
        initServerData();
    }


    void initServerData() {
        UI ui = UI.getCurrent();
        ThreadUtil.execAsync(() -> {
            try {

                Server server = new Server();
                server.copyTo();
                //  cpu
                Cpu cpu = server.getCpu();
                List<ServerVo.CpuVo> cpuVoList = ListUtil.of(
                        new ServerVo.CpuVo("核心数", cpu.getCpuNum()),
                        new ServerVo.CpuVo("用户使用率", cpu.getUsed()),
                        new ServerVo.CpuVo("系统使用率", cpu.getSys()),
                        new ServerVo.CpuVo("当前空闲率", cpu.getFree())
                );
                //  mem
                Mem mem = server.getMem();
                Jvm jvm = server.getJvm();
                List<ServerVo.MemVo> memVoList = ListUtil.of(
                        new ServerVo.MemVo("总内存", mem.getTotal(), jvm.getTotal()),
                        new ServerVo.MemVo("已用内存", mem.getUsed(), jvm.getUsed()),
                        new ServerVo.MemVo("剩余内存", mem.getFree(), jvm.getFree()),
                        new ServerVo.MemVo("使用率", mem.getUsage(), jvm.getUsage())
                );
                //  系统信息
                Sys sys = server.getSys();
                List<ServerVo.HostVo> sysVoList = ListUtil.of(
                        new ServerVo.HostVo("服务器名称", sys.getComputerName(), "操作系统", sys.getOsName()),
                        new ServerVo.HostVo("服务器IP", sys.getComputerIp(), "系统架构", sys.getOsArch())
                );
                //  jvm信息
                List<ServerVo.HostVo> jvmVoList = ListUtil.of(
                        new ServerVo.HostVo("Java名称", jvm.getName(), "Java版本", jvm.getVersion()),
                        new ServerVo.HostVo("启动时间", jvm.getStartTime(), "运行时长", jvm.getRunTime()),
                        new ServerVo.HostVo("安装路径", jvm.getHome(), "项目路径", sys.getUserDir())
                );
                //  disk信息
                List<SysFile> sysFiles = server.getSysFiles();
                List<ServerVo.DiskVo> diskVoList = new ArrayList<>();
                for (SysFile sysFile : sysFiles) {
                    ServerVo.DiskVo diskVo = new ServerVo.DiskVo()
                            .setDirName(sysFile.getDirName())
                            .setSysTypeName(sysFile.getSysTypeName())
                            .setTypeName(sysFile.getTypeName())
                            .setTotal(sysFile.getTotal())
                            .setFree(sysFile.getFree())
                            .setUsed(sysFile.getUsed())
                            .setUsage(sysFile.getUsage() + "%");
                    diskVoList.add(diskVo);
                }
                ui.access(() -> {
                    tblCpu.setDataSource(cpuVoList)
                          .setLoading(false);
                    tblMem.setDataSource(memVoList)
                          .setLoading(false);
                    tblSys.setDataSource(sysVoList)
                          .setLoading(false);
                    tblJvm.setDataSource(jvmVoList)
                          .setLoading(false);
                    tblDisk.setDataSource(diskVoList)
                           .setLoading(false);
                });
            } catch (Exception e) {
                e.printStackTrace();
                // todo 获取失败时的处理
            }
        });
    }


    // *****************************************************************************************************************
    //                                    样式
    // *****************************************************************************************************************

    void initStyle() {
        // 样式
        this.getStyle().setPaddingLeft("12px").setPaddingRight("12px");
    }
}
