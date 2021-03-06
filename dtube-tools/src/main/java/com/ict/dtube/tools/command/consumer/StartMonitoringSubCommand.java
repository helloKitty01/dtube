/**
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ict.dtube.tools.command.consumer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;

import com.ict.dtube.client.log.ClientLogger;
import com.ict.dtube.tools.command.SubCommand;
import com.ict.dtube.tools.monitor.DefaultMonitorListener;
import com.ict.dtube.tools.monitor.MonitorConfig;
import com.ict.dtube.tools.monitor.MonitorService;


/**
 * 启动监控
 * 
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @since 2014-7-5
 */
public class StartMonitoringSubCommand implements SubCommand {
    private final Logger log = ClientLogger.getLog();


    @Override
    public String commandName() {
        return "startMonitoring";
    }


    @Override
    public String commandDesc() {
        return "Start Monitoring";
    }


    @Override
    public Options buildCommandlineOptions(Options options) {
        return options;
    }


    @Override
    public void execute(CommandLine commandLine, Options options) {
        try {
            MonitorService monitorService =
                    new MonitorService(new MonitorConfig(), new DefaultMonitorListener());

            monitorService.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
        }
    }
}
