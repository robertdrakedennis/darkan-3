package com.undercut.ui.backend.dsl.scopes

import com.undercut.cache.type.sprites.Sprite
import com.undercut.game.Skill
import com.undercut.script.api.getXp
import com.undercut.ui.backend.dsl.ImGuiState
import com.undercut.ui.backend.dsl.boolState
import com.undercut.ui.backend.dsl.commands.*
import com.undercut.ui.backend.dsl.utils.*
import com.undercut.ui.backend.dsl.utils.ImGuiColors.hex
import com.undercut.ui.backend.flags.InputTextFlags
import com.undercut.ui.backend.native.ImGuiTexture
import com.undercut.ui.backend.rendering.CommandRenderer
import com.undercut.util.getLevelForXp
import com.undercut.util.getXpForLevel
import java.util.concurrent.atomic.AtomicReference

/**
 * Shared DSL extensions that work on both WindowScope and ChildScope via ImGuiScope.
 */

// Text & layout
internal fun LayoutScope.text(text: String) { commands.add(TextCommand(text)) }
internal fun LayoutScope.textWrapped(text: String) { commands.add(TextWrappedCommand(text)) }
internal fun LayoutScope.separator() { commands.add(SeparatorCommand) }
internal fun LayoutScope.sameLine() { commands.add(SameLineCommand) }
internal fun LayoutScope.newLine() { commands.add(NewLineCommand) }
internal fun LayoutScope.spacing() { commands.add(SpacingCommand) }

// Buttons
internal fun LayoutScope.button(label: String, width: Float = 0f, height: Float = 0f, onClick: () -> Unit) {
    commands.add(PushStyleColorCommand(ImGuiCol.Text, hex("#000")))
    commands.add(ButtonCommand(label, width, height, onClick))
    commands.add(PopStyleColorCommand(1))
}
internal fun LayoutScope.smallButton(label: String, onClick: () -> Unit) { commands.add(SmallButtonCommand(label, onClick)) }

// Inputs
internal fun LayoutScope.checkbox(label: String, state: ImGuiState<Boolean>) { commands.add(CheckboxCommand(label, state)) }
internal fun LayoutScope.checkbox(label: String, currentValue: Boolean, onChange: (Boolean) -> Unit) {
    commands.add(CheckboxActionValueCommand(label, currentValue, onChange))
}
internal fun LayoutScope.inputText(label: String, textState: ImGuiState<String>, flags: InputTextFlags = InputTextFlags.None) {
    commands.add(InputTextCommand(label, textState, flags.value))
}
internal fun LayoutScope.inputText(label: String, currentValue: String, maxLength: Int = 256, flags: InputTextFlags = InputTextFlags.None, onChange: (String) -> Unit) {
    commands.add(InputTextActionValueCommand(label, currentValue, maxLength, flags.value, onChange))
}
internal fun LayoutScope.inputInt(label: String, state: ImGuiState<Int>, step: Int = 1, stepFast: Int = 10) {
    commands.add(InputIntCommand(label, state, step, stepFast))
}
internal fun LayoutScope.inputInt(label: String, currentValue: Int, step: Int = 1, stepFast: Int = 10, onChange: (Int) -> Unit) {
    commands.add(InputIntActionValueCommand(label, currentValue, step, stepFast, onChange))
}

// Sliders
internal fun LayoutScope.sliderFloat(label: String, state: ImGuiState<Float>, min: Float, max: Float) {
    commands.add(SliderFloatCommand(label, state, min, max))
}
internal fun LayoutScope.sliderInt(label: String, state: ImGuiState<Int>, min: Int, max: Int) {
    commands.add(SliderIntCommand(label, state, min, max))
}

// Scroll helpers
internal fun LayoutScope.getScrollY(): Float { val r = AtomicReference(0f); commands.add(GetScrollYCommand(r)); return r.get() }
internal fun LayoutScope.getScrollMaxY(): Float { val r = AtomicReference(100f); commands.add(GetScrollMaxYCommand(r)); return r.get() }
internal fun LayoutScope.setScrollHereY(ratio: Float) { commands.add(SetScrollHereYCommand(ratio)) }

// Color widgets
internal fun LayoutScope.colorEdit4(label: String, r: ImGuiState<Float>, g: ImGuiState<Float>, b: ImGuiState<Float>, a: ImGuiState<Float>, flags: Int = 0) {
    commands.add(ColorEdit4Command(label, r, g, b, a, flags))
}
internal fun LayoutScope.colorPicker4(label: String, r: ImGuiState<Float>, g: ImGuiState<Float>, b: ImGuiState<Float>, a: ImGuiState<Float>, flags: Int = 0) {
    commands.add(ColorPicker4Command(label, r, g, b, a, flags))
}

// Combo
internal inline fun LayoutScope.combo(label: String, preview: String, block: ComboScope.() -> Unit): Boolean {
    val stateKey = "combo_${label}"
    val isOpen = CommandRenderer.getSharedState(stateKey) { boolState(false) }
    commands.add(PushStyleColorCommand(ImGuiCol.Button, hex("#ffffff11")))
    commands.add(PushStyleColorCommand(ImGuiCol.ButtonHovered, hex("#ffffff22")))
    commands.add(PushStyleColorCommand(ImGuiCol.ButtonActive, hex("#ffffff33")))
    commands.add(BeginComboCommand(label, preview, isOpen))
    commands.add(PopStyleColorCommand(3))

    val scope = ComboScope()
    scope.block()
    commands.addAll(scope.commands)

    commands.add(EndComboCommand())
    return isOpen.value
}
internal fun LayoutScope.combo(label: String, currentItem: ImGuiState<Int>, items: List<String>, maxItemsShown: Int = -1) {
    commands.add(PushStyleColorCommand(ImGuiCol.Button, hex("#ffffff11")))
    commands.add(PushStyleColorCommand(ImGuiCol.ButtonHovered, hex("#ffffff22")))
    commands.add(PushStyleColorCommand(ImGuiCol.ButtonActive, hex("#ffffff33")))
    commands.add(ComboCommand(label, currentItem, items, maxItemsShown))
    commands.add(PopStyleColorCommand(3))
}
internal fun LayoutScope.combo(label: String, currentIndex: Int, items: List<String>, maxItemsShown: Int = -1, onChange: (Int) -> Unit) {
    commands.add(PushStyleColorCommand(ImGuiCol.Button, hex("#ffffff11")))
    commands.add(PushStyleColorCommand(ImGuiCol.ButtonHovered, hex("#ffffff22")))
    commands.add(PushStyleColorCommand(ImGuiCol.ButtonActive, hex("#ffffff33")))
    commands.add(ComboActionValueCommand(label, currentIndex, items, maxItemsShown, onChange))
    commands.add(PopStyleColorCommand(3))
}

// Table
internal inline fun LayoutScope.table(id: String, columns: Int, flags: Int = 0, block: TableScope.() -> Unit): Boolean {
    val key = "last_scope_table_${id}_${columns}_${flags}"
    val last = CommandRenderer.getSharedState(key) { boolState(false) }
    val result = AtomicReference(false)
    commands.add(BeginTableCommand(id, columns, flags, result))

    val scope = TableScope()
    scope.block()
    commands.addAll(scope.commands)

    commands.add(EndTableCommand())
    commands.add(SetBoolStateFromRefCommand(last, result))
    return last.value
}

internal fun LayoutScope.tableSetupColumn(label: String, flags: Int = 0, width: Float = 0f) { commands.add(TableSetupColumnCommand(label, flags, width)) }
internal fun LayoutScope.tableHeadersRow() { commands.add(TableHeadersRowCommand()) }
internal fun LayoutScope.tableNextRow() { commands.add(TableNextRowCommand()) }
internal fun LayoutScope.tableNextColumn() { commands.add(TableNextColumnCommand()) }

// ListBox
internal inline fun LayoutScope.listBox(label: String, sizeX: Float = 0f, sizeY: Float = 0f, block: ListBoxScope.() -> Unit): Boolean {
    val key = "last_listBox_${label}_${sizeX}_${sizeY}"
    val last = CommandRenderer.getSharedState(key) { boolState(false) }
    val result = AtomicReference(false)
    commands.add(BeginListBoxCommand(label, sizeX, sizeY, result))

    val scope = ListBoxScope()
    scope.block()
    commands.addAll(scope.commands)

    commands.add(EndListBoxCommand())
    commands.add(SetBoolStateFromRefCommand(last, result))
    return last.value
}

// Group / styles shared wrappers
internal inline fun LayoutScope.group(block: ChildScope.() -> Unit) {
    commands.add(BeginGroupCommand())
    val scope = ChildScope(); scope.block(); commands.addAll(scope.commands)
    commands.add(EndGroupCommand())
}
internal inline fun LayoutScope.styleVar(styleVar: ImGuiStyleVar, value: Float, block: ChildScope.() -> Unit) {
    commands.add(PushStyleVarFloatCommand(styleVar, value))
    val scope = ChildScope(); scope.block(); commands.addAll(scope.commands)
    commands.add(PopStyleVarCommand(1))
}
internal inline fun LayoutScope.styleVar(styleVar: ImGuiStyleVar, x: Float, y: Float, block: ChildScope.() -> Unit) {
    commands.add(PushStyleVarVec2Command(styleVar, x, y))
    val scope = ChildScope(); scope.block(); commands.addAll(scope.commands)
    commands.add(PopStyleVarCommand(1))
}
internal inline fun LayoutScope.styleColor(colorIndex: ImGuiCol, color: Int, block: ChildScope.() -> Unit) {
    commands.add(PushStyleColorCommand(colorIndex, color))
    val scope = ChildScope(); scope.block(); commands.addAll(scope.commands)
    commands.add(PopStyleColorCommand(1))
}
internal inline fun LayoutScope.itemWidth(width: Float, block: ChildScope.() -> Unit) {
    commands.add(PushItemWidthCommand(width))
    val scope = ChildScope(); scope.block(); commands.addAll(scope.commands)
    commands.add(PopItemWidthCommand())
}

// Shared queries (available in any layout scope)
internal fun LayoutScope.isItemHovered(): Boolean {
    val last = CommandRenderer.getSharedState("last_isItemHovered") { boolState(false) }
    val result = AtomicReference(false)
    commands.add(IsItemHoveredQuery(result))
    commands.add(SetBoolStateFromRefCommand(last, result))
    return last.value
}

internal fun LayoutScope.isItemClicked(mouseButton: Int = 0): Boolean {
    val key = "last_isItemClicked_${mouseButton}"
    val last = CommandRenderer.getSharedState(key) { boolState(false) }
    val result = AtomicReference(false)
    commands.add(IsItemClickedQuery(mouseButton, result))
    commands.add(SetBoolStateFromRefCommand(last, result))
    return last.value
}

// Widgets and inputs shared across scopes
internal fun LayoutScope.progressBar(
    fraction: Float,
    sizeX: Float = -1f,
    sizeY: Float = 0f,
    overlay: String? = null
) { commands.add(ProgressBarCommand(fraction, sizeX, sizeY, overlay)) }

internal fun LayoutScope.dragFloat(
    label: String,
    state: ImGuiState<Float>,
    speed: Float = 1f,
    min: Float = 0f,
    max: Float = 0f,
    format: String = "%.3f",
    flags: Int = 0
) { commands.add(DragFloatCommand(label, state, speed, min, max, format, flags)) }

internal fun LayoutScope.dragInt(
    label: String,
    state: ImGuiState<Int>,
    speed: Float = 1f,
    min: Int = 0,
    max: Int = 0,
    format: String = "%d",
    flags: Int = 0
) { commands.add(DragIntCommand(label, state, speed, min, max, format, flags)) }

internal fun LayoutScope.inputTextMultiline(
    label: String,
    textState: ImGuiState<String>,
    sizeX: Float = 0f,
    sizeY: Float = 0f,
    flags: InputTextFlags = InputTextFlags.None
) { commands.add(InputTextMultilineCommand(label, textState, sizeX, sizeY, flags.value)) }

internal fun LayoutScope.inputTextMultiline(
    label: String,
    currentValue: String,
    maxLength: Int = 1024,
    sizeX: Float = 0f,
    sizeY: Float = 0f,
    flags: InputTextFlags = InputTextFlags.None,
    onChange: (String) -> Unit
) { commands.add(InputTextMultilineActionValueCommand(label, currentValue, maxLength, sizeX, sizeY, flags.value, onChange)) }

internal fun LayoutScope.inputFloat2(
    label: String,
    x: ImGuiState<Float>,
    y: ImGuiState<Float>,
    format: String = "%.3f",
    flags: InputTextFlags = InputTextFlags.None
) { commands.add(InputFloat2Command(label, x, y, format, flags.value)) }

internal fun LayoutScope.inputFloat3(
    label: String,
    x: ImGuiState<Float>,
    y: ImGuiState<Float>,
    z: ImGuiState<Float>,
    format: String = "%.3f",
    flags: InputTextFlags = InputTextFlags.None
) { commands.add(InputFloat3Command(label, x, y, z, format, flags.value)) }

internal fun LayoutScope.inputFloat4(
    label: String,
    x: ImGuiState<Float>,
    y: ImGuiState<Float>,
    z: ImGuiState<Float>,
    w: ImGuiState<Float>,
    format: String = "%.3f",
    flags: InputTextFlags = InputTextFlags.None
) { commands.add(InputFloat4Command(label, x, y, z, w, format, flags.value)) }

internal fun LayoutScope.colorEdit3(
    label: String,
    r: ImGuiState<Float>,
    g: ImGuiState<Float>,
    b: ImGuiState<Float>,
    flags: Int = 0
) {
    val dummyAlpha = object : ImGuiState<Float>() { override var value: Float = 1f; override val buffer = null }
    commands.add(ColorEdit4Command(label, r, g, b, dummyAlpha, flags))
}

internal fun LayoutScope.image(
    texture: ImGuiTexture,
    sizeX: Float,
    sizeY: Float,
    uv0X: Float = 0f,
    uv0Y: Float = 0f,
    uv1X: Float = 1f,
    uv1Y: Float = 1f,
    tintColor: Int = -1,
    borderColor: Int = 0
) { commands.add(ImageCommand(texture, sizeX, sizeY, uv0X, uv0Y, uv1X, uv1Y, tintColor, borderColor)) }


internal fun LayoutScope.imageButton(
    texture: ImGuiTexture,
    sizeX: Float,
    sizeY: Float,
    uv0X: Float = 0f,
    uv0Y: Float = 0f,
    uv1X: Float = 1f,
    uv1Y: Float = 1f,
    framePadding: Int = -1,
    bgColor: Int = 0,
    tintColor: Int = -1,
    onClick: () -> Unit
) { commands.add(ImageButtonCommand(texture, sizeX, sizeY, uv0X, uv0Y, uv1X, uv1Y, framePadding, bgColor, tintColor, onClick)) }

// Watermark: draw a texture anchored to a window corner without affecting layout
internal fun LayoutScope.watermark(
    texture: ImGuiTexture,
    corner: Corner = Corner.BottomRight,
    paddingX: Float = 0f,
    paddingY: Float = 0f,
    alpha: Float = 1f,
    scale: Float = 1f
) {
    commands.add(WindowWatermarkCommand(texture, corner, paddingX, paddingY, alpha, scale))
}

// Three-slice image button helpers
internal fun LayoutScope.triSliceImageButton(
    id: String,
    left: ImGuiTexture,
    middle: ImGuiTexture,
    right: ImGuiTexture,
    width: Float,
    height: Float = 0f,
    tileMiddle: Boolean = true,
    label: String? = null,
    labelColor: Int = ImGuiColors.WHITE,
    onClick: () -> Unit
) {
    commands.add(TriSliceImageButtonCommand(id, left, middle, right, width, height, tileMiddle, label, labelColor, onClick))
}

internal fun LayoutScope.triSliceImageButtonFromSprites(
    id: String,
    leftSpriteId: Int,
    middleSpriteId: Int,
    rightSpriteId: Int,
    width: Float,
    height: Float = 0f,
    tileMiddle: Boolean = true,
    label: String? = null,
    labelColor: Int = ImGuiColors.WHITE,
    onClick: () -> Unit
) {
    commands.add(TriSliceImageButtonFromIdsCommand(id, leftSpriteId, middleSpriteId, rightSpriteId, width, height, tileMiddle, label, labelColor, onClick))
}

// Window decoration: nine-slice sprite skin (drawn on window drawlist)
internal fun LayoutScope.windowSpriteSkin(skin: SpriteNineSlice, tileEdges: Boolean = true, tileCenter: Boolean = true) {
    commands.add(WindowNineSliceCommand(skin, tileEdges, tileCenter))
}

internal fun LayoutScope.windowSpriteSkinFromIds(
    topLeft: Int,
    top: Int,
    topRight: Int,
    left: Int,
    center: Int,
    right: Int,
    bottomLeft: Int,
    bottom: Int,
    bottomRight: Int,
    tileEdges: Boolean = true,
    tileCenter: Boolean = true
) {
    val skin = SpriteNineSlice.fromSpriteIds(topLeft, top, topRight, left, center, right, bottomLeft, bottom, bottomRight)
    commands.add(WindowNineSliceCommand(skin, tileEdges, tileCenter))
}

// Convenience: draw skin, offset content by left/top border, and constrain width/height by right/bottom border
internal inline fun LayoutScope.windowSpriteSkinContent(
    skin: SpriteNineSlice,
    tileEdges: Boolean = true,
    tileCenter: Boolean = true,
    block: ChildScope.() -> Unit
) {
    // Draw skin first (behind content)
    commands.add(WindowNineSliceCommand(skin, tileEdges, tileCenter))
    // Offset initial cursor so items don't overlap left/top borders
    commands.add(SetCursorPosCommand(skin.left.width.toFloat(), skin.top.height.toFloat()))
    // Constrain content so it doesn't underlap right/bottom borders
    val scope = ChildScope();
    commands.add(BeginChildCommand(
        id = "__skin_content__",
        width = -skin.right.width.toFloat(),
        height = -skin.bottom.height.toFloat(),
        result = AtomicReference(false),
        childFlags = 0,
        windowFlags = 0
    ))
    scope.block()
    commands.addAll(scope.commands)
    commands.add(EndChildCommand())
}

internal fun LayoutScope.applyBackgroundOverlay(
    overlay: ImGuiTexture,
    alpha: Float,
    tiled: Boolean = true
) {
    commands.add(WindowBackgroundOverlayCommand(overlay, alpha, tiled))
}

internal inline fun LayoutScope.windowSpriteSkinContentFromIds(
    topLeft: Int,
    top: Int,
    topRight: Int,
    left: Int,
    center: Int,
    right: Int,
    bottomLeft: Int,
    bottom: Int,
    bottomRight: Int,
    tileEdges: Boolean = true,
    tileCenter: Boolean = true,
    block: ChildScope.() -> Unit
) {
    val skin = SpriteNineSlice.fromSpriteIds(topLeft, top, topRight, left, center, right, bottomLeft, bottom, bottomRight)
    windowSpriteSkinContent(skin, tileEdges, tileCenter, block)
}

// TabBar helper available in any LayoutScope (not just WindowScope)
internal inline fun LayoutScope.tabBar(
    id: String,
    flags: Int = 0,
    block: TabBarScope.() -> Unit
): Boolean {
    val key = "last_tabBar_${id}_${flags}"
    val last = CommandRenderer.getSharedState(key) { boolState(false) }
    val result = AtomicReference(false)
    commands.add(BeginTabBarCommand(id, flags, result))

    val scope = TabBarScope()
    scope.block()
    commands.addAll(scope.commands)

    commands.add(EndTabBarCommand())
    commands.add(SetBoolStateFromRefCommand(last, result))
    return last.value
}

// Scrollable child with sprite-styled vertical scrollbar.
// Reserves space using ImGui style ScrollbarSize and overlays sprites; default scrolling remains functional.
internal inline fun LayoutScope.scrollableWithSpriteScrollbar(
    id: String,
    width: Float = 0f,
    height: Float = 0f,
    trackSpriteId: Int,
    thumbSpriteId: Int,
    tileTrack: Boolean = true,
    minThumbPx: Float = 24f,
    childFlags: Int = 0,
    windowFlags: Int = 0,
    block: ChildScope.() -> Unit
) {
    val trackW = try { Sprite.get(trackSpriteId).maxWidth.toFloat().coerceAtLeast(1f) } catch (_: Throwable) { 12f }
    // Reserve space for the custom scrollbar by setting ScrollbarSize
    commands.add(PushStyleVarFloatCommand(ImGuiStyleVar.ScrollbarSize, trackW))

    val result = AtomicReference(false)
    commands.add(BeginChildCommand(id, width, height, result, childFlags, windowFlags))
    val scope = ChildScope(); scope.block(); commands.addAll(scope.commands)
    // Draw the overlay on top of the child content
    commands.add(OverlayVScrollbarCommand(trackSpriteId, thumbSpriteId, tileTrack, minThumbPx, drawAboveAll = true))
    commands.add(EndChildCommand())
    commands.add(PopStyleVarCommand(1))
}

// Layout helpers
internal fun LayoutScope.indent(width: Float = 0f) { commands.add(IndentCommand(width)) }
internal fun LayoutScope.unindent(width: Float = 0f) { commands.add(UnindentCommand(width)) }
internal fun LayoutScope.setNextItemWidth(width: Float) { commands.add(SetNextItemWidthCommand(width)) }
internal fun LayoutScope.setCursorPos(x: Float, y: Float) { commands.add(SetCursorPosCommand(x, y)) }
internal fun LayoutScope.setCursorPosX(x: Float) { commands.add(SetCursorPosXCommand(x)) }
internal fun LayoutScope.setCursorPosY(y: Float) { commands.add(SetCursorPosYCommand(y)) }
internal fun LayoutScope.alignTextToFramePadding() { commands.add(AlignTextToFramePaddingCommand()) }

// Legacy columns API
internal fun LayoutScope.columns(count: Int = 1, id: String? = null, border: Boolean = true) { commands.add(ColumnsCommand(count, id, border)) }
internal fun LayoutScope.nextColumn() { commands.add(NextColumnCommand()) }
internal fun LayoutScope.setColumnWidth(columnIndex: Int, width: Float) { commands.add(SetColumnWidthCommand(columnIndex, width)) }

// Tree
internal fun LayoutScope.treeNode(label: String, flags: Int = ImGuiTreeNodeFlags.None): Boolean { val r = AtomicReference(false); commands.add(TreeNodeCommand(label, flags, r)); return r.get() }
internal fun LayoutScope.treePush(id: String) { commands.add(TreePushCommand(id)) }
internal fun LayoutScope.treePop() { commands.add(TreePopCommand()) }
internal fun LayoutScope.collapsingHeader(label: String, flags: Int = 0): Boolean { val r = AtomicReference(false); commands.add(CollapsingHeaderCommand(label, flags, r)); return r.get() }

// CollapsingHeader with child block
internal inline fun LayoutScope.collapsingHeader(label: String, flags: Int = 0, block: ChildScope.() -> Unit): Boolean {
    val result = AtomicReference(false)
    val result2 = AtomicReference(false)
    commands.add(BeginChildCommand(label, 0f, 0f, result2, ImGuiChildFlags.Border or ImGuiChildFlags.AutoResizeY or ImGuiChildFlags.AlwaysAutoResize))
    commands.add(CollapsingHeaderCommand(label, flags, result))

    val scope = ChildScope()
    scope.block()
    commands.addAll(scope.commands)

    commands.add(EndCollapsingHeaderCommand())
    commands.add(EndChildCommand())
    return result.get() && result2.get()
}

// Style push/pop (immediate-style, not block wrappers)
internal fun LayoutScope.pushStyleVar(styleVar: ImGuiStyleVar, value: Float) { commands.add(PushStyleVarFloatCommand(styleVar, value)) }
internal fun LayoutScope.pushStyleVar(styleVar: ImGuiStyleVar, x: Float, y: Float) { commands.add(PushStyleVarVec2Command(styleVar, x, y)) }
internal fun LayoutScope.popStyleVar(count: Int = 1) { commands.add(PopStyleVarCommand(count)) }
internal fun LayoutScope.pushStyleColor(colorIndex: ImGuiCol, color: Int) { commands.add(PushStyleColorCommand(colorIndex, color)) }
internal fun LayoutScope.popStyleColor(count: Int = 1) { commands.add(PopStyleColorCommand(count)) }

// Selectable (stateless): label + isSelected + onClick. Leak-free across render loops.
internal fun LayoutScope.selectable(label: String, isSelected: Boolean, onClick: () -> Unit) {
    commands.add(SelectableActionValueCommand(label, isSelected, onClick))
}
internal fun LayoutScope.pushItemWidth(width: Float) { commands.add(PushItemWidthCommand(width)) }
internal fun LayoutScope.popItemWidth() { commands.add(PopItemWidthCommand()) }


// Queries
internal fun LayoutScope.getItemRectMin(): Pair<Float, Float> { val r = AtomicReference(Pair(0f,0f)); commands.add(GetItemRectMinQuery(r)); return r.get() }
internal fun LayoutScope.getItemRectMax(): Pair<Float, Float> { val r = AtomicReference(Pair(0f,0f)); commands.add(GetItemRectMaxQuery(r)); return r.get() }
internal fun LayoutScope.getItemRectSize(): Pair<Float, Float> { val r = AtomicReference(Pair(0f,0f)); commands.add(GetItemRectSizeQuery(r)); return r.get() }
internal fun LayoutScope.isItemActive(): Boolean { val r = AtomicReference(false); commands.add(IsItemActiveQuery(r)); return r.get() }
internal fun LayoutScope.isItemFocused(): Boolean { val r = AtomicReference(false); commands.add(IsItemFocusedQuery(r)); return r.get() }
internal fun LayoutScope.isItemVisible(): Boolean { val r = AtomicReference(false); commands.add(IsItemVisibleQuery(r)); return r.get() }
internal fun LayoutScope.getWindowPos(): Pair<Float, Float> { val r = AtomicReference(Pair(0f,0f)); commands.add(GetWindowPosQuery(r)); return r.get() }
internal fun LayoutScope.getWindowSize(): Pair<Float, Float> { val r = AtomicReference(Pair(0f,0f)); commands.add(GetWindowSizeQuery(r)); return r.get() }
internal fun LayoutScope.getContentRegionAvail(): Pair<Float, Float> { val r = AtomicReference(Pair(0f,0f)); commands.add(GetContentRegionAvailQuery(r)); return r.get() }
internal fun LayoutScope.getMousePos(): Pair<Float, Float> { val r = AtomicReference(Pair(0f,0f)); commands.add(GetMousePosQuery(r)); return r.get() }
internal fun LayoutScope.isMouseDown(button: Int): Boolean { val r = AtomicReference(false); commands.add(IsMouseDownQuery(button, r)); return r.get() }
internal fun LayoutScope.isMouseClicked(button: Int, repeat: Boolean = false): Boolean { val r = AtomicReference(false); commands.add(IsMouseClickedQuery(button, repeat, r)); return r.get() }
internal fun LayoutScope.isMouseDoubleClicked(button: Int): Boolean { val r = AtomicReference(false); commands.add(IsMouseDoubleClickedQuery(button, r)); return r.get() }
internal fun LayoutScope.getCursorPos(): Pair<Float, Float> { val r = AtomicReference(Pair(0f,0f)); commands.add(GetCursorPosQuery(r)); return r.get() }
internal fun LayoutScope.getColumnWidth(columnIndex: Int): Float { val r = AtomicReference(0f); commands.add(GetColumnWidthQuery(columnIndex, r)); return r.get() }

// BeginChild shared helper
internal inline fun LayoutScope.child(
    id: String,
    width: Float = 0f,
    height: Float = 0f,
    childFlags: Int = 0,
    windowFlags: Int = 0,
    block: ChildScope.() -> Unit
) {
    val result = AtomicReference(false)
    commands.add(BeginChildCommand(id, width, height, result, childFlags, windowFlags))
    val scope = ChildScope(); scope.block(); commands.addAll(scope.commands)
    commands.add(EndChildCommand())
}

internal fun LayoutScope.xpProgressBar(
    skill: Skill,
    width: Float = -1f,
    height: Float = 19f,
    padding: Float = 0f
) {
    val actualWidth = if (width < 0) {
        val availableWidth = getContentRegionAvail().first
        (availableWidth - padding * 2).coerceAtLeast(100f)
    } else width

    val currentXp = getXp(skill)
    val currentLevel = getLevelForXp(currentXp)
    val xpForCurrentLevel = getXpForLevel(currentLevel)
    val xpForNextLevel = getXpForLevel(currentLevel + 1)
    val progressXp = currentXp - xpForCurrentLevel
    val totalXpNeeded = xpForNextLevel - xpForCurrentLevel
    val progressPercent = if (totalXpNeeded > 0) progressXp.toFloat() / totalXpNeeded else 0f

    text(if (currentLevel >= 120) "Maxed Level" else "Level $currentLevel")
    if (currentLevel < 120)
        progressBar(progressPercent, actualWidth, height)
}