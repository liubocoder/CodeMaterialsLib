package com.lb.plugin

import org.gradle.api.Plugin;
import org.gradle.api.Project

class Register implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.logger.error "================自定义插件成功！=========="
    }
}