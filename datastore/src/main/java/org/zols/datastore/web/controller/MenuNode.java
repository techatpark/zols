package org.zols.datastore.web.controller;

import java.util.Map;
import java.util.HashMap;

public class MenuNode{
  String name;
  Map<String, MenuNode> children;

  public MenuNode(String menuName){
    name  = menuName;
    children  = new HashMap<String, MenuNode>();
  }

  public MenuNode addChildMenu(String menu){
    MenuNode child = new MenuNode(menu);
    this.children.put(menu,child);
    return child;
  }

  public String getMenuName(){
    return this.name;
  }

  public Map<String, MenuNode> getChildMenus(){
    return this.children;
  }
}
