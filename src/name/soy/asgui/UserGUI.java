package name.soy.asgui;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class UserGUI {
	Player who;
	GUI what;
	int radius;
	Location openLocation;

	public UserGUI(Player who, GUI what) {
		this.radius = 0;

		this.openCount = 0;

		this.openEntries = new LinkedList<>();

		this.who = who;
		this.what = what;
		this.openLocation = who.getLocation().clone();
		this.task = Bukkit.getScheduler().runTaskTimer(AsGUI.getPlugin(), () -> {
			if (this.openEntries.size() == 0) {
				this.task.cancel();
			} else if (Math.abs(( this.openEntries.get(0)).nowat - this.previus) > 0.11D) {
				if (this.openEntries.get(0).nowat > this.previus) {
					for (UserGUIEntry<?> userGUIEntry : this.openEntries) {
						userGUIEntry.nowat = (float) (userGUIEntry.nowat - 0.1D);
					}
					focus(who.getLocation().getYaw());
				} else {
					for (UserGUIEntry<?> userGUIEntry : this.openEntries) {
						userGUIEntry.nowat = (float) (userGUIEntry.nowat + 0.1D);
					}
					focus(who.getLocation().getYaw());
				}
			}

			if (this.radius < 20) {
				this.radius++;
			}
			for (UserGUIEntry<?> userGUIEntry : this.openEntries) {
				userGUIEntry.update();
			}
		}, 1, 1L);
	}

	int previus;
	int openCount;
	BukkitTask task;
	UserGUIEntry<?> foc;
	List<UserGUIEntry<?>> openEntries;

	public void close() {
		this.task.cancel();
		for (UserGUIEntry<?> userGUIEntry : this.openEntries) {
			userGUIEntry.remove();
		}
	}

	public void rotate(boolean plus) {
		if (plus) {
			this.previus++;
		} else {
			this.previus--;
		}
	}

	public UserGUI open() {
		for (GUIEntry entry : this.what.entries) {
			if (entry.canshow(this.who)) {
				this.openCount++;
			}
		}
		int c = 0;
		this.previus = -2;
		for (int i = 0; i < this.what.entries.size(); i++) {

			GUIEntry entry = (GUIEntry) this.what.entries.get(i);
			if (entry.canshow(this.who)) {
				AsUserGUIEntry asentry = new AsUserGUIEntry(this, entry, c);
				asentry.show();
				this.openEntries.add(asentry);

				c++;
			}
		}

		focus(this.who.getLocation().getYaw());
		return this;
	}

	public void focus(float yaw) {
		yaw %= 360.0F;
		yaw = (yaw < 0.0F) ? (yaw + 360.0F) : yaw;
		for (UserGUIEntry<?> e : this.openEntries) {
			double r = (360.0D / this.openCount * e.nowat + this.openLocation.getYaw()) % 360.0D;
			r = (r < 0.0D) ? (r + 360.0D) : r;

			double d = yaw + r;
			if (Math.abs(360.0D - Math.abs(d)) < (180 / this.openCount)) {
				if (this.foc == null) {
					this.foc = e;
					this.foc.getFocus();
					continue;
				}
				if (!this.foc.equals(e)) {

					e.getFocus();
					this.foc.lostFocus();
					this.foc = e;
				}
			}
		}
	}
}
