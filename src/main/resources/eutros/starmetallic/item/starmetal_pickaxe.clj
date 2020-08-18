(ns eutros.starmetallic.item.starmetal-pickaxe
  (:import (net.minecraft.item PickaxeItem
                               Item$Properties
                               IItemTier
                               ItemStack)
           net.minecraft.world.World
           net.minecraft.entity.Entity
           hellfirepvp.astralsorcery.common.item.base.AlignmentChargeRevealer)
  (:use eutros.starmetallic.lib.specific-proxy
        eutros.starmetallic.lib.obfuscation
        eutros.starmetallic.item.common))

(def starmetal_pickaxe
  (let [do-regen (create-regen 200                          ;; starlight every
                               100                          ;; ticks
                               )]
    (sproxy
      [PickaxeItem AlignmentChargeRevealer]
      [;; tier
       ^IItemTier tool-tier

       ;; attackDamage
       ^int (identity 5)

       ;; attackSpeed
       ^float (identity 5.)

       ;; properties
       ^Item$Properties default-properties]

      ((!m 'func_77663_a                                    ;; inventoryTick
           )
       [^ItemStack stack
        ^World world
        ^Entity entity
        ^int slot
        ^boolean isSelected]
       (do-regen stack world entity slot isSelected))

      ('shouldCauseReequipAnimation
        [^ItemStack oldStack
         ^ItemStack newStack
         ^boolean slotChanged]
        (should-reequip oldStack newStack slotChanged)))))

starmetal_pickaxe
