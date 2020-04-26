package name.soy.asgui;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

public class AsUserGUIEntry extends UserGUIEntry<GUIEntry> {
	ArmorStand as;

	public AsUserGUIEntry(UserGUI userGUI, GUIEntry entry, int c) {
		super(userGUI, entry);
		this.nowat = c;
		this.sitem = entry.item.clone();
		if (this.sitem.getType().equals(Material.PLAYER_HEAD)) {
			SkullMeta meta = (SkullMeta) this.sitem.getItemMeta();
			if (!meta.hasOwner()) {
				meta.setOwningPlayer(this.who);
				this.sitem.setItemMeta(meta);
			}
		}
	}

	ArmorStand item;

	ItemStack sitem;
	boolean inexecute = false;
	public boolean show() {
		if (this.entry.canshow(this.who)) {
			double radian = Math
					.toRadians(360.0D / this.userGUI.openCount * this.nowat + this.userGUI.openLocation.getYaw());
			double distance = this.userGUI.what.distance / 20.0D * this.userGUI.radius;
			Vector vec = new Vector(Math.sin(radian) * distance, 0.0D, Math.cos(radian) * distance);
			Location show = this.who.getLocation().clone().add(vec);
			show.setYaw(360.0F / this.userGUI.openCount * this.entry.at - 180.0F);
			this.as = (ArmorStand) show.getWorld().spawnEntity(show, EntityType.ARMOR_STAND);
			//让它只显示头
			this.as.setVisible(false);
			//别让它掉下去啊！
			this.as.setGravity(false);
			//盔甲架太吵了，安静一点.
			this.as.setSilent(true);
			this.as.addScoreboardTag("as-gui-item");
			this.as.setInvulnerable(true);
			this.as.setCustomName(AsGUI.getPlugin().PAPIed(who, this.entry.name));
			this.as.setCustomNameVisible(true);
			this.as.setMarker(false);
			if (!this.sitem.getType().isBlock()) {
				this.item = (ArmorStand) show.getWorld().spawnEntity(show.subtract(0.0D, 0.3D, 0.0D),
						EntityType.ARMOR_STAND);
				this.item.setVisible(false);
				this.item.setGravity(false);
				this.item.setSilent(true);
				this.item.setInvulnerable(true);
				this.item.setHelmet(this.sitem);
				this.item.setMarker(false);
			} else {
				this.as.setHelmet(this.sitem);
			}

			return true;
		}
		return false;
	}

	public void getFocus() {
		this.as.setCustomName(AsGUI.getPlugin().PAPIed(who,this.entry.focus));
		this.sitem.addUnsafeEnchantment(Enchantment.MENDING, 1);
		(this.entry.item.getType().isBlock() ? this.as : this.item).setHelmet(this.sitem);
	}

	public void lostFocus() {
		this.as.setCustomName(AsGUI.getPlugin().PAPIed(who,this.entry.name));
		this.sitem.removeEnchantment(Enchantment.MENDING);
		(this.entry.item.getType().isBlock() ? this.as : this.item).setHelmet(this.sitem);
	}

	public void update() {
		if (this.as != null) {
			radian = Math
					.toRadians(360.0D / this.userGUI.openCount * this.nowat + this.userGUI.openLocation.getYaw());
			double distance = this.userGUI.what.distance / 20.0D * this.userGUI.radius;
			Vector vec = new Vector(Math.sin(radian) * distance, 0.0D, Math.cos(radian) * distance);
			Location show = this.userGUI.openLocation.clone().add(vec);
			show.setYaw((float) -Math.toDegrees(radian) - 180.0F);

			this.as.teleport(show);

			if (this.item != null) {
				this.item.teleport(show.subtract(0.0D, 0.3D, 0.0D));
			}
		} else {
			show();
		}
	}

	public void remove() {
		this.as.remove();
		if (this.item != null) {
			this.item.remove();
		}
		this.sitem = null;
	}

	public void execute() {
		if(inexecute)return;
		inexecute = true;
		Bukkit.getScheduler().runTaskLater(AsGUI.getPlugin(), ()->inexecute=false, 5);
		for (String s : this.entry.cmds) {
			
			if (s.startsWith("server:")) {
				AsGUI.ToServer(this.who, s.split(":")[1]);
				continue;
			}
			if (s.startsWith("op:")) {
				boolean stillop = who.isOp();
				who.setOp(true);
				Bukkit.dispatchCommand(who,
						s.substring(3).replace("%player", this.who.getName()));
				who.setOp(stillop);
				continue;
			}
			if (s.startsWith("console:")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						s.substring(8).replace("%player", this.who.getName()));
				continue;
			}
			Bukkit.dispatchCommand(this.who, s.replace("%player", this.who.getName()));
		}
	}
}
