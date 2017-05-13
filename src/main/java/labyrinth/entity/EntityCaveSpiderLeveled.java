package labyrinth.entity;

import java.util.List;

import labyrinth.util.LevelUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityCaveSpiderLeveled extends EntityCaveSpider implements IMobLeveled {

	public EntityCaveSpiderLeveled(World worldIn) {
		super(worldIn);
	}
	
	/**Because cave spider venomous it is nerfed compared to regular spider**/
	@Override
	public void setLevel(int levelIn) {
		this.experienceValue=LevelUtil.getExperienceValue(levelIn);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(LevelUtil.getMaxHealth(levelIn)/4d);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(LevelUtil.getMovementSpeed(levelIn)/4d);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(LevelUtil.getAttackDamage(levelIn)/4d);
        this.setHealth(this.getMaxHealth());
	}
	
	int level = 0;
	ResourceLocation lootTable;
	
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("level", this.level);
		compound.setString("lootTable",lootTable.toString());
	}

	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.level = compound.getInteger("level");
		this.experienceValue = LevelUtil.getExperienceValue(level);
		this.lootTable = new ResourceLocation(compound.getString("lootTable"));
	}

	@Override
	public void setLootTable(ResourceLocation lootTableIn) {
		lootTable = lootTableIn;
	}
	
	@Override
    protected ResourceLocation getLootTable()
    {
        return lootTable;
    }
	
	/**
	 * Leveled creatures shall not despawn.
	 */
	protected boolean canDespawn() {
		return false;
	}

	/**
	 * Remove despawn.
	 */
	protected void despawnEntity() {}
	/**
	 * Do not update entities too far from player (to avoid lag).
	 */
	Entity nearestPlayer = null;
	
	@Override
	public void onUpdate() {
		if (!world.isRemote) {
			if (nearestPlayer != null) {
				int dx = (int) (nearestPlayer.posX - this.posX);
				int dy = (int) (nearestPlayer.posY - this.posY);
				int dz = (int) (nearestPlayer.posZ - this.posZ);
				if (dy * dy * 16 + dx * dx + dz * dz > 6144) {
					nearestPlayer = null;
					return;
				}
			} else {
				for (EntityPlayer player:this.getEntityWorld().playerEntities) {
					int dx = (int) (player.posX - this.posX);
					int dy = (int) (player.posY - this.posY);
					int dz = (int) (player.posZ - this.posZ);
					if (dy * dy * 16 + dx * dx + dz * dz < 4096) {
						nearestPlayer = player;
					}
				}
				if (nearestPlayer == null) {
					return;
				}
			}
		}
		super.onUpdate();
	}}
