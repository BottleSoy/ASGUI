package name.soy.asgui;

import org.bukkit.entity.Player;






public abstract class UserGUIEntry<T extends GUIEntry>
  extends Object
{
  protected T entry;
  protected Player who;
  protected UserGUI userGUI;
  protected float nowat;
  
  public UserGUIEntry(UserGUI gui, T entry) {
    this.entry = entry;
    this.userGUI = gui;
    this.who = this.userGUI.who;
  }
  
  public abstract boolean show();
  
  public abstract void remove();
  
  public abstract void getFocus();
  
  public abstract void update();
  
  public abstract void lostFocus();
  
  public abstract void execute();
}
