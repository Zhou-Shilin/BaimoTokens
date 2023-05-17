# BaimoTokens

BaimoTokens 是一个 Bukkit 插件，用于为玩家生成随机令牌并将其存储在 MySQL 数据库中。该令牌可用于验证玩家在网站或其他外部系统上的身份。

## 安装

1. 将 `BaimoTokens.jar` 文件复制到 Bukkit 服务器的 `plugins` 目录中。
2. 编辑 `config.yml` 文件以匹配您的 MySQL 服务器设置。
3. 启动 Bukkit 服务器。

## 使用

要生成令牌，玩家可以使用 `/token` 命令。这将生成一个 12 位数字和字母的令牌，有效期为 1 小时。令牌将存储在 `baimotokens` MySQL 表中，同时还记录了玩家的 UUID 和用户名。

## 配置

`config.yml` 文件包含以下设置：

| 设置      | 描述           | 默认值      |
|-----------|----------------|-------------|
| `host`    | MySQL 服务器主机名 | `localhost` |
| `port`    | MySQL 服务器端口 | `3306`      |
| `database` | MySQL 数据库名称 | `baimotokens` |
| `username` | MySQL 用户名      | `root`      |
| `password` | MySQL 密码        | `password`  |

## 许可证

BaimoTokens 使用 [MIT 许可证](LICENSE)。

## 贡献

欢迎贡献代码。请 Fork 此仓库并提交 Pull Request 视为申请审核。