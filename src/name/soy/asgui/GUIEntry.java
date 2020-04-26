package name.soy.asgui;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GUIEntry
{
  String name;
  String focus;
  String permission;
  List<String> cmds;
  ItemStack item;
  int at;
  /**
   * 能否给玩家显示
   * @param who
   * @return
   */
  public boolean canshow(Player who) {
    if (this.permission == null) {
      return true;
    }
    if (this.permission.startsWith("!")) {
      return !who.hasPermission(this.permission.substring(1));
    }
    return who.hasPermission(this.permission);
  }
}
