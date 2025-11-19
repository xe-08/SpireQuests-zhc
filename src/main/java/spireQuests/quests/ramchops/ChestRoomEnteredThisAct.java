package spireQuests.quests.ramchops;

import spireQuests.Anniv8Mod;

public class ChestRoomEnteredThisAct {

    static boolean chestRoomEntered = false;


    public static void onEnterChestRoom(){
        chestRoomEntered = true;
//        Anniv8Mod.logger.warn("You have entered the chest room this act!");
    }

    public static void onEnterNewAct(){
        chestRoomEntered = false;
//        Anniv8Mod.logger.warn("Chest room entry status has been reset!");
    }


}
