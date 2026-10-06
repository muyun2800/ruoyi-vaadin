package com.ruoyi.ui.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ServerVo
 *
 * @author 何德海
 * @version 2026/04/09
 **/

public class ServerVo {

    private ServerVo(){
        // nop
    }

    @Data
    @Accessors(chain = true)
    public static class CpuVo {

        String name;

        Object value;

        public CpuVo() {
        }

        public CpuVo(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }


    @Data
    @Accessors(chain = true)
    public static class MemVo {

        String name;

        Object mem;

        Object jvm;

        public MemVo() {
        }

        public MemVo(String name, Object mem, Object jvm) {
            this.name = name;
            this.mem = mem;
            this.jvm = jvm;
        }
    }


    @Data
    @Accessors(chain = true)
    public static class HostVo {

        String col1;

        Object col2;

        Object col3;

        Object col4;

        public HostVo() {
        }

        public HostVo(String col1, Object col2, Object col3, Object col4) {
            this.col1 = col1;
            this.col2 = col2;
            this.col3 = col3;
            this.col4 = col4;
        }
    }

    @Data
    @Accessors(chain = true)
    public static class DiskVo {

        String dirName;

        String sysTypeName;

        String typeName;

        String total;

        String free;

        String used;

        String usage;


    }


}