# Nested Shulkers (Fabric, Minecraft 26.3)

In creative mode you can put shulker boxes inside shulker boxes.

## Build
Requires JDK 25.
    ./gradlew build        (Windows: gradlew.bat build)
Jar: build/libs/nestedshulkers-1.0.0.jar -> put in your mods folder.

The Gradle wrapper (9.8.2) is included, so no separate Gradle install is needed.

## Test
1. Creative world, place a shulker, put another shulker in it.
2. Break it (creative drops it with contents) or Ctrl+middle-click it.
3. Hold the item, press C+<1-9> to save to a hotbar, restart without the mod,
   load with X+<1-9>, place it: inner shulker is still there.

## Notes
- Both sides check the rule, so it works in singleplayer / on servers that also have the mod.
  A vanilla server will reject the insert.
- Deep nesting full of items can get huge; very large stacks can exceed packet/save limits.

## Copy item as /give command
Press K (rebindable under Controls > Nested Shulkers):
- hovering any item in an inventory screen (hotbar row included), or
- with no screen open, to copy the item in your hand.
The full `/give @s item[components] count` command goes to your clipboard.
Tooltips show a "[K] Copy as /give command" hint. Tooltips themselves can't be clicked.
Long commands (nested shulkers) exceed the chat box limit; run them from a command block or function.
