package cn.sh1rocu.slashblade.mixin.accessor;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)
public interface MobAccessor {
    // 开发环境被其他mod使用AW改为public，服务端不一定会安装上述mod，因此使用Accessor来访问
    @Accessor("goalSelector")
    GoalSelector sb$getGoalSelector();
}
