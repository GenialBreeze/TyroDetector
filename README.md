## Tyro Detector
一个为Bukkit服务端提供的Minecraft新手行为监测插件。

### 插件功能：

1. 提供自定义式的方块放置与破坏、物品存取和实体死亡监测。
2. 提供GUI式的物品配置文件生成功能。
3. 提供邮件提醒功能，可自定义一个发件邮箱和多个收件人。邮件系统分两部分：周期邮件报告和紧急邮件。自定义邮件周期，自动报告周期内的玩家可疑操作。自定义玩家可疑操作阈值，达到后优先周期邮件发送关于该玩家操作的紧急邮件。

### 使用

下载最新的[release](https://github.com/IceBlues/TyroDetector/releases)版本

放入plugins文件夹

### 权限

tyro.base 基础权限

### 配置文件

提供了默认的示例配置文件，请在插件首次启动后按照自己的需求修改配置文件。

### 命令

```
/tyro config 打开物品配置文件生成GUI

/tyro white {add|remove|list} <playername> 白名单操作

/tyro reload 重载配置文件(白名单配置除外)
```

