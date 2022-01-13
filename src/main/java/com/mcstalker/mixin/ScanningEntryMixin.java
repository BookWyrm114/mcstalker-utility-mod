package com.mcstalker.mixin;

import com.mcstalker.screen.ServerDiscoveryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultiplayerServerListWidget.class)
public class ScanningEntryMixin extends AlwaysSelectedEntryListWidget<MultiplayerServerListWidget.Entry> {

	@Shadow
	@Final
	private MultiplayerServerListWidget.Entry scanningEntry;

	public ScanningEntryMixin(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	@Redirect(method = "updateEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget;addEntry(Lnet/minecraft/client/gui/widget/EntryListWidget$Entry;)I"))
	public int updateEntries(MultiplayerServerListWidget instance, EntryListWidget.Entry<?> entry) {
		if (!(this.client.currentScreen instanceof ServerDiscoveryScreen)) {
			return this.addEntry(this.scanningEntry);
		} else {
			return 0;
		}
	}
}