package name.soy.asgui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GUI {
	public String name;
	public List<GUIEntry> entries;
	public double distance;
	public String permission;
	public boolean moveable;

	public UserGUI showToPlayer(Player who) {
		return (new UserGUI(who, this)).open();
	}

	private GUI(String name) {
		this.name = name;
	}
	//通过配置文件创建GUI
	public static GUI create(ConfigurationSection config) {
		GUI gui = null;
		String name = config.getName();
		gui = new GUI(name);
		try {
			gui.distance = config.getDouble("distance");
			gui.permission = config.getString("permission");
			gui.moveable = config.getBoolean("moveable");
			ConfigurationSection entries = config.getConfigurationSection("entry");
			gui.entries = new ArrayList<>(entries.getKeys(false).size());
			int pos = 0;
			try {
				for (String key : entries.getKeys(false)) {
					ConfigurationSection entry = entries.getConfigurationSection(key);
					GUIEntry entry1 = new GUIEntry();
					entry1.at = pos;
					entry1.name = entry.getString("name");
					Map<String, Object> o = entry.getConfigurationSection("item").getValues(false);
					o.put("v", 1519);
					entry1.item = ItemStack.deserialize(o);
					entry1.permission = entry.getString("permission");
					entry1.cmds = entry.getStringList("command");
					entry1.focus = entry.getString("focName");
					gui.entries.add(entry1);
				}
				pos++;
			} catch (Exception e) {
				Bukkit.getLogger().severe("GUI:" + name + ",pos:" + pos);
				e.printStackTrace();
			}
		} catch (Exception e) {
			Bukkit.getLogger().severe("GUI:" + name);
			e.printStackTrace();
		}
		return gui;
	}
}
