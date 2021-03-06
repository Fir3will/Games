package customcode.vectest;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import main.utils.math.Vector2F;

public abstract class EntityVT implements Serializable
{
	private static int ID;
	private int id;
	public float width, height, x, y, xV, yV;
	public boolean isDestroyed = false;
	protected TestFrame main;

	public EntityVT(TestFrame main, int x, int y, int width, int height)
	{
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
		this.id = ID++;
		this.main = main;

		main.sprites.add(this);
	}

	public abstract void drawSprite(Graphics2D g2d, ImageObserver obs);

	public Rectangle getBounds()
	{
		return new Rectangle((int) x, (int) y, (int) width, (int) height);
	}

	public final float getWidth()
	{
		return width;
	}

	public final float getX()
	{
		return x + width / 2;
	}

	public final float getY()
	{
		return y + height / 2;
	}

	public final boolean isDestroyed()
	{
		return isDestroyed;
	}

	public final void init()
	{
		if (main.isPaused) return;

		try
		{
			if (this.getClass().getDeclaredField("health") != null)
			{
				int health = this.getClass().getDeclaredField("health").getInt(this);

				if (health <= 0)
				{
					setDestroyed(true);
				}
			}
		}
		catch (Exception e)
		{}

		x += xV;
		y += yV;
		run();
	}

	public abstract void run();

	public final void setDestroyed(boolean d)
	{
		isDestroyed = d;
	}

	public final Vector2F getLocation()
	{
		return new Vector2F(getX(), getY());
	}

	public final void moveTo(Vector2F pos, float speed)
	{
		speed = 75 / speed;
		Vector2F us = getLocation();
		Vector2F them = pos.clone();

		if (pos.getX() > getX())
		{
			xV = us.clone().setY(0).distance(them.clone().setY(0)) / speed;
		}
		else
		{
			xV = -(us.clone().setY(0).distance(them.clone().setY(0)) / speed);
		}
		if (pos.getY() > getY())
		{
			yV = us.clone().setX(0).distance(them.clone().setX(0)) / speed;
		}
		else
		{
			yV = -(us.clone().setX(0).distance(them.clone().setX(0)) / speed);
		}
	}

	public final EntityVT getClosestTo(Class<? extends EntityVT> clazz)
	{
		HashMap<Float, EntityVT> map = new HashMap<Float, EntityVT>();

		for (EntityVT sprite : main.sprites)
		{
			if (sprite.getClass().equals(clazz))
			{
				map.put(sprite.getDistanceTo(getLocation()), sprite);
			}
		}

		Iterator<Entry<Float, EntityVT>> iterator = map.entrySet().iterator();
		float min = Float.MAX_VALUE;
		EntityVT minS = null;

		while (iterator.hasNext())
		{
			Entry<Float, EntityVT> entry = iterator.next();

			if (entry.getKey() < min)
			{
				min = entry.getKey();
				minS = entry.getValue();
			}
		}

		return minS;
	}

	public final float getDistanceTo(Vector2F vec)
	{
		return vec.distance(getLocation());
	}

	public final void moveTo(EntityVT sprite, float speed)
	{
		if (this.equals(sprite)) return;

		moveTo(sprite.getLocation(), speed);
	}

	public void collidedWith(EntityVT sprite)
	{

	}

	/**
	 * Indicates whether this SpriteVT is equivalent to the parameter SpriteVT
	 * This is checked via the Sprite ID and {@link #hashCode()} 
	 */
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof EntityVT)) return false;
		if (this == o || hashCode() == o.hashCode()) return true;

		return false;
	}

	/**
	 * Returns the Sprite's distinct id
	 */
	@Override
	public int hashCode()
	{
		return id;
	}

	private static final long serialVersionUID = 1L;
}
