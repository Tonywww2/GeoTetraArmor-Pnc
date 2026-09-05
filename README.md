# GeoTetraArmor PneumaticCraft Compat

A Minecraft 1.20.1 Forge compatibility mod that adds PneumaticCraft pneumatic armor as Tetra attachments for GeoTetraArmor.

## Features

- Pneumatic attachments for the head, chest, legs, and feet extra slots.
- Attachment-only access to compressed air, PNC upgrades, armor settings, HUD, and tooltips.
- Installing an attachment preserves the source PNC armor's air, upgrades, settings, and colors.
- Removing or replacing an attachment returns the matching PNC armor with its current state.
- Charging Station and slot-specific PneumaticCraft armor behavior.

## Requirements

- Minecraft 1.20.1
- Forge 47+
- Tetra 6.9.0
- GeoTetraArmor
- PneumaticCraft: Repressurized 6.0+

Install this mod and all dependencies on both the client and server.

## Build

```powershell
.\gradlew.bat build
```

The built JAR is written to `build/libs/`.

## 中文

这是一个适用于 Minecraft 1.20.1 Forge 的兼容模组，将 PneumaticCraft 气动护甲作为 Tetra 附件加入 GeoTetraArmor。

- 为头部、胸部、腿部和脚部的额外槽位添加气动附件。
- 只有安装附件的护甲才能储气、使用 PNC 升级、设置、HUD 和提示。
- 安装时继承原 PNC 护甲的气量、升级、设置与颜色。
- 拆除或替换时返还保留当前状态的对应 PNC 护甲。
- 客户端和服务端均需安装本模组及全部前置模组。

详细实现导航见 [PROJECT_INDEX.md](PROJECT_INDEX.md)，CurseForge 项目介绍见 [CURSEFORGE_DESCRIPTION.md](CURSEFORGE_DESCRIPTION.md)。
