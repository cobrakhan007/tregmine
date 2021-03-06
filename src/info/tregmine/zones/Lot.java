package info.tregmine.zones;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.tregmine.quadtree.Rectangle;

import info.tregmine.api.TregminePlayer;

public class Lot
{
    private int id;
    private int zoneId;
    private String name;
    private Rectangle rect;
    private Set<Integer> owners;

    public Lot()
    {
        this.owners = new HashSet<Integer>();
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getZoneId()
    {
        return zoneId;
    }

    public void setZoneId(int zoneId)
    {
        this.zoneId = zoneId;
    }

    public Set<Integer> getOwners()
    {
        return owners;
    }

    public void setOwner(List<Integer> owners)
    {
        this.owners.addAll(owners);
    }

    public boolean isOwner(TregminePlayer player)
    {
        return owners.contains(player.getId());
    }

    public void addOwner(TregminePlayer player)
    {
        owners.add(player.getId());
    }

    public void deleteOwner(TregminePlayer player)
    {
        owners.remove(player.getId());
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Rectangle getRect()
    {
        return rect;
    }

    public void setRect(Rectangle rect)
    {
        this.rect = rect;
    }
}
