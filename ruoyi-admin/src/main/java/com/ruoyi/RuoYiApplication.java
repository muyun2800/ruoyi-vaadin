package com.ruoyi;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.NoTheme;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author ruoyi
 */
@SpringBootApplication( scanBasePackages = {"com.ruoyi"}, exclude = {DataSourceAutoConfiguration.class})
@Push
@NoTheme
public class RuoYiApplication implements AppShellConfigurator {
    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(RuoYiApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  若依启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                           " .-------.       ____     __        \n" +
                           " |  _ _   \\      \\   \\   /  /    \n" +
                           " | ( ' )  |       \\  _. /  '       \n" +
                           " |(_ o _) /        _( )_ .'         \n" +
                           " | (_,_).' __  ___(_ o _)'          \n" +
                           " |  |\\ \\  |  ||   |(_,_)'         \n" +
                           " |  | \\ `'   /|   `-'  /           \n" +
                           " |  |  \\    /  \\      /           \n" +
                           " ''-'   `'-'    `-..-'              ");
    }


}
