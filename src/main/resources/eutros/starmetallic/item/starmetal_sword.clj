(ns eutros.starmetallic.item.starmetal-sword
  (:import (net.minecraft.item SwordItem
                               Item$Properties
                               IItemTier
                               ItemStack)
           net.minecraft.world.World
           net.minecraft.entity.Entity
           net.minecraft.entity.player.PlayerEntity
           eutros.starmetallic.packets.PacketBurst
           (net.minecraftforge.event.entity.player PlayerInteractEvent$LeftClickEmpty
                                                   AttackEntityEvent)
           (net.minecraft.util Hand SoundCategory)
           java.util.function.Consumer
           hellfirepvp.astralsorcery.common.lib.SoundsAS
           hellfirepvp.astralsorcery.common.item.base.AlignmentChargeRevealer
           (net.minecraftforge.fml.network.simple SimpleChannel)
           (hellfirepvp.astralsorcery.common.constellation ConstellationItem IWeakConstellation IMinorConstellation)
           (java.util List))
  (:use eutros.clojurelib.lib.class-gen
        eutros.starmetallic.lib.obfuscation
        eutros.starmetallic.item.common
        eutros.starmetallic.packets))

(def do-regen
  (create-regen 200                                         ;; starlight every
                100                                         ;; ticks
                ))

#rip/rip ^{}
(defclass
  SMSword (:extends SwordItem) (:implements AlignmentChargeRevealer ConstellationItem)

  (:constructor []
    (super [;; tier
            ^IItemTier tool-tier

            ;; attackDamage
            ^int (identity 5)

            ;; attackSpeed
            ^float (identity 5.)

            ;; properties
            ^Item$Properties default-properties]))

  #rip/client ^void
  (:method #obf/obf ^{:obf/srg func_77624_a} addInformation
    [^ItemStack stack
     ^World worldIn
     ^List tooltip
     ^net.minecraft.client.util.ITooltipFlag flagIn]
    (add-information this stack tooltip))

  (:method #obf/obf ^{:obf/srg func_77663_a} inventoryTick
    [^ItemStack stack ^World world ^Entity entity ^int slot ^boolean isSelected]
    (do-regen stack world entity slot isSelected))

  ^boolean
  (:method shouldCauseReequipAnimation
    [^ItemStack oldStack
     ^ItemStack newStack
     ^boolean slotChanged]
    (should-reequip oldStack newStack slotChanged))

  ^IWeakConstellation
  (:method getAttunedConstellation
    [^ItemStack stack]
    (get-constellation stack TAG_ATTUNED IWeakConstellation))

  ^boolean
  (:method setAttunedConstellation
    [^ItemStack stack
     ^IWeakConstellation cst]
    (set-constellation stack cst TAG_ATTUNED IWeakConstellation))

  ^IMinorConstellation
  (:method getTraitConstellation
    [^ItemStack stack]
    (get-constellation stack TAG_TRAIT IMinorConstellation))

  ^boolean
  (:method setTraitConstellation
    [^ItemStack stack
     ^IMinorConstellation cst]
    (set-constellation stack cst TAG_TRAIT IMinorConstellation)))

(def starmetal-sword (SMSword.))

(defn check-stack
  [^ItemStack stack]
  (and (not (! stack (func_190926_b                         ;; isEmpty
                       )))
       (-> stack
           (! (func_77973_b                                 ;; getItem
                ))
           (= starmetal-sword))))

(defn summon-burst
  [^PlayerEntity player]
  (when (and (check-stack
               (! player (func_184614_ca                    ;; getHeldItemMainhand
                           )))
             (= (! player (func_184825_o                    ;; getCooledAttackStrength
                            0))
                1.))
    (as-> ((ns-resolve 'eutros.starmetallic.entity.starlight-burst
                       '->EntityBurst) player) $
          (!! player
            field_70170_p                                   ;; world
            (func_217411_a                                  ;; addEntity
              $)))
    (!! player
      (func_184614_ca                                       ;; getHeldItemMainhand
        )
      (func_222118_a                                        ;; damageItem
        1 player
        (reify Consumer
          (accept [_ p]
            (! p (func_213334_d                             ;; sendBreakAnimation
                   Hand/MAIN_HAND))))))
    (!! player
      field_70170_p                                         ;; world
      (func_184148_a                                        ;; playSound
        ^PlayerEntity (identity nil)
        (! player (func_226277_ct_                          ;; getPosX
                    ))
        (! player (func_226278_cu_                          ;; getPosY
                    ))
        (! player (func_226281_cx_                          ;; getPosZ
                    ))
        SoundsAS/ILLUMINATION_WAND_LIGHT
        SoundCategory/PLAYERS
        0.8
        1.4))))

(defn event-leftclickempty
  [^PlayerInteractEvent$LeftClickEmpty evt]
  (when (check-stack (.getItemStack evt))
    (.sendToServer ^SimpleChannel CHANNEL (PacketBurst.))))

starmetal-sword
