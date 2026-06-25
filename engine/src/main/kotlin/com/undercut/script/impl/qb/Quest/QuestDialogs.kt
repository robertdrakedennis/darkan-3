package com.undercut.script.impl.qb.Quest

import com.undercut.game.interfaces.IFSlot
import com.undercut.script.api.dialogueOptions
import com.undercut.script.api.interfaces
import com.undercut.script.api.inventory
import com.undercut.script.api.varps

class QuestDialogs {
    companion object {
        var autoCloseEnable = false
        var optionNumbers = mutableListOf<DialogOption>()
        var usedOptionNumbers = mutableListOf<DialogOption>()
        var hasAnyOption = false
        var optionNumber = -1
        var questDialogOptions = mutableListOf<String>()

        fun dialog1188() {
            val number = getDialogueNumber()

            val option = when (number) {
                1 -> 8
                2 -> 13
                3 -> 18
                4 -> 23
                5 -> 28
                else -> -1
            }


            if (option != -1) {
                println("Interacting with option: $option")
                // Use IFSlot API instead of interactComponent
                IFSlot(1188, option, -1).dialogueContinue()
            } else {
                if (optionNumbers.isEmpty() && !hasAnyOption) {
                    if (autoCloseEnable) {
                        println("Option not found - Closing dialog")
                        IFSlot(1188, 4, -1).click()
                    }
                } else {
                    println(hasAnyOption.toString())
                    if (hasAnyOption)
                        optionNumber = 1
                    println("Any option instruction - interacting with $optionNumber")
                    when (optionNumber) {
                        1 -> IFSlot(1188, 8, -1).dialogueContinue()
                        2 -> IFSlot(1188, 13, -1).dialogueContinue()
                        3 -> IFSlot(1188, 18, -1).dialogueContinue()
                        4 -> IFSlot(1188, 23, -1).dialogueContinue()
                        5 -> IFSlot(1188, 28, -1).dialogueContinue()
                        else -> IFSlot(1188, 4, -1).click()
                    }
                }
            }
        }

        fun isDialogOpen(): Boolean {
            return interfaces.isOpen(1188) || interfaces.isOpen(1184) || interfaces.isOpen(1191) ||
                    interfaces.isOpen(1193) || interfaces.isOpen(1500) || interfaces.isOpen(1189) ||
                    interfaces.isOpen(1186) || interfaces.isOpen(720) || interfaces.isOpen(1370) ||
                    interfaces.isOpen(1251) || interfaces.isOpen(847) || interfaces.isOpen(1187) || interfaces.isOpen(94) ||
                    varps.getVarBit(21222) == 1 || interfaces.isOpen(1244)
        }

        fun dialog1188pick(num: Int) {
            val option = when (num) {
                1 -> 8
                2 -> 13
                3 -> 18
                4 -> 23
                5 -> 28
                else -> -1
            }

            if (option != -1) {
                IFSlot(1188, option, -1).dialogueContinue()
            }
        }

        fun pressDialog() {
            when {
                interfaces.isOpen(1187) && interfaces.getComponent(1187,20)!=null-> IFSlot(1187, 20, -1).dialogueContinue()
                interfaces.isOpen(94) && interfaces.getComponent(94,6)!=null-> {
                    IFSlot(94, 6, -1).click()
                }

                interfaces.isOpen(1188) -> {
                    if (varps.getVarBit(5326) == 25) {
                        val num = varps.getVarBit(5327)
                        when (num) {
                            1, 5, 9 -> dialog1188pick(1)
                            2, 6, 10 -> dialog1188pick(2)
                            3, 11, 7 -> dialog1188pick(3)
                            4, 8, 12 -> dialog1188pick(4)
                        }
                    } else {
                        dialog1188()
                    }

                }

                interfaces.isOpen(1184) && interfaces.getComponent(1184,15)!=null-> {
                    // Get dialog text from interface component
//                    val dialogText = interfaces.getComponent(1184, 15)?.text ?: ""
//                    println(dialogText)
//
//                    val matched = AutoCloseDialogs.entries.any { topic ->
//                        dialogText.contains(topic.phrase)
//                    }

                    // if (matched) {
                    IFSlot(1184, 15, -1).dialogueContinue()
                    // } else {
                    //      IFSlot(1184, 15, -1).click()
                    // }
                }
                interfaces.isOpen(1224) && interfaces.getComponent(1224,21)!=null-> IFSlot(1224, 21, -1).click()
                interfaces.isOpen(1244) && interfaces.getComponent(1244,21)!=null-> IFSlot(1244, 21, -1).click()

                interfaces.isOpen(1187) && interfaces.getComponent(1187,3)!=null-> IFSlot(1187, 3, -1).click()
                interfaces.isOpen(1191) && interfaces.getComponent(1191,15)!=null-> IFSlot(1191, 15, -1).dialogueContinue()
                interfaces.isOpen(1193) -> println("@ me in disc if u see this")
                interfaces.isOpen(1500) && interfaces.getComponent(1500,409)!=null-> IFSlot(1500, 409, -1).click()
                interfaces.isOpen(1189) && interfaces.getComponent(1189,19)!=null-> IFSlot(1189, 19, -1).dialogueContinue()
                interfaces.isOpen(1186) && interfaces.getComponent(1186,8)!=null-> IFSlot(1186, 8, -1).dialogueContinue()
                interfaces.isOpen(720) && interfaces.getComponent(720,1)!=null-> IFSlot(720, 1, -1).dialogueContinue()

                interfaces.isOpen(1370) && interfaces.getComponent(1370,30)!=null-> IFSlot(1370, 30, -1).dialogueContinue()
                interfaces.isOpen(847) && interfaces.getComponent(847,3)!=null-> IFSlot(847, 3, -1).click()
                interfaces.isOpen(960) && interfaces.getComponent(960,3)!=null-> IFSlot(960, 3, -1).click()
                continueHandler() -> IFSlot(955, 15, -1).click()
            }
        }

        fun setAnyOption(b: Boolean) {
            hasAnyOption = b
            optionNumber = 1
        }

        fun setAnyOption(b: Boolean, num: Int) {
            hasAnyOption = b
            optionNumber = num
        }

        fun resetDialogOptions() {
            optionNumber = -1
            optionNumbers = mutableListOf()
            usedOptionNumbers = mutableListOf()
        }

        fun updateQuestDialogOptions(dialogOptions: List<String>) {
            questDialogOptions = dialogOptions.toMutableList()
        }

        fun getDialogueNumber(): Int {
            // Get dialog options from interface
            val dialogOptions = dialogueOptions

            if (dialogOptions.isNotEmpty() && DebugScript.instance.currentQuest != DebugScript.Quest.TEST_DONTSELECT) {
                println("Dialog Options: $dialogOptions")

                for (dialogue in questDialogOptions) {
                    println(dialogue)


                    if (dialogOptions.containsKey(dialogue)) {
                        var searchSpecificDialogOption = dialogue

                        // // Handle specific quest dialog options
                        // if (DebugScript.instance.currentQuest == DebugScript.Quest.IN_SEARCH_OF_THE_MYREQUE) {
                        //     println("Handling specific dialog option for In Search of the Myreque")
                        //     val title = interfaces.getComponent(1188, 2)?.text?.lowercase() ?: ""
                        //     searchSpecificDialogOption = when (title) {
                        //         "who is the youngest member of the myreque?" -> "Ivan Strom."
                        //         "name the only female member of the myreque." -> "Sani Piliu."
                        //         "who is the leader of the myreque?" -> "Veliaf Hurtz."
                        //         "what family is rumoured to rule morytania?" -> "Drakan."
                        //         "which member of the myreque was originally a scholar?",
                        //         "who was previously a scholar?" -> "Polmafi Ferdygris."
                        //         "what does myreque mean?" -> "Hidden in Myre."
                        //         "what is the boatman's name?" -> "Cyreg Paddlehorn."
                        //         else -> dialogue
                        //     }
                        // }

                        println("Checking for $searchSpecificDialogOption")

                        // Find matching option
                        for ((optionText, slot) in dialogOptions) {
                            if (optionText == searchSpecificDialogOption) {
                                println("searchSpecificDialogOption Exists")
                                // Extract option number from the slot component
                                val option = when (slot.componentId) {
                                    8 -> 1
                                    13 -> 2
                                    18 -> 3
                                    23 -> 4
                                    28 -> 5
                                    else -> -1
                                }

                                if (option != -1) {
                                    val dialogOption = DialogOption().apply {
                                        this.option = searchSpecificDialogOption
                                        this.optionNumber = option
                                    }

                                    val alreadyOnList = optionNumbers.any {
                                        it.option == dialogOption.option && it.optionNumber == dialogOption.optionNumber
                                    }

                                    if (!alreadyOnList) {
                                        println("Option not found, adding it")
                                        optionNumbers.add(dialogOption)
                                    } else {
                                        println("Option already on list, skipping")
                                    }
                                    break
                                }
                            }
                        }
                    }
                }

                // Remove used options
                usedOptionNumbers.forEach { used ->
                    optionNumbers.removeAll { it.option == used.option }
                }

                if (optionNumbers.isNotEmpty()) {
                    println("Before sorting")
                    optionNumbers.sortBy { it.optionNumber }
                    println("After sorting")
                }

                return when {
                    optionNumbers.size > 1 -> {
                        println("Interacting with option: ${optionNumbers[0].option} Option: ${optionNumbers[0].optionNumber}")
                        val optionValue = optionNumbers[0]
                        usedOptionNumbers.add(optionValue)
                        optionValue.optionNumber
                    }

                    optionNumbers.size == 1 -> {
                        println("Interacting with option: ${optionNumbers[0].option} Option: ${optionNumbers[0].optionNumber}")
                        usedOptionNumbers.add(optionNumbers[0])
                        optionNumbers[0].optionNumber
                    }

                    else -> {
                        println("No option found - Returning -1")
                        -1
                    }
                }
            }

            println("Default Returning -1")
            return -1
        }

        fun hasItem(item: String): Boolean {
            return inventory.hasItem(item)
        }

        fun continueHandler(): Boolean {
            val thing = interfaces.getComponent(955, 16)
            return thing != null && !thing.text.isNullOrEmpty() && thing.text.isNotBlank()
        }

        fun println(msg: String) {
            kotlin.io.println(msg)
        }
    }

    class DialogOption {
        var option: String = ""
        var optionNumber: Int = 0
    }

    enum class AutoCloseDialogs(val phrase: String) {
        REDBERRY_PIE("redberry pie. They REALLY like redberry pie."),
        BARAEK("If I were you I would talk to Baraek,"),
        BLACK_ARM("The ruthless and notorious Black Arm "),
        PET_SHOP_OWNER("Is there anything else i can help you with?"),
        KING_RONALD("I've told you everything I know."),
        DUTCHNESS("Let us leave the duchess alone,"),
        VELIAF("While you're there, you could see if that murderer"),
        FLORIN("Listen, if you do manage to find a way to get a place here,"),
        RAZVAN("Hmm, perhaps you'd consider fixing up the general store."),
        CORNELIUS("Fix the floopin bank would ya!"),
        AUREL("Please can you fix the bank booth first"),
        FATHER_URHNEY("Can I have a look at it?"),
        VERTIDA("What should I do now?");
    }

    enum class QuestInstruction(val text: String, val quest: DebugScript.Quest) {
        // SOUL_SEARCHING_INSTRUCTION("Have all item in inventory when starting the quest.", DebugScript.Quest.SOUL_SEARCHING),
        // DEATH_PLATEAU_INSTRUCTION("Have level 5 Agility and 10 Attack/Strength", DebugScript.Quest.DEATH_PLATEAU),
        // ERNEST_THE_CHICKEN_INSTRUCTION("Solve basement puzzle manually \r\n Bring Weapons to kill a level 15 skelly", DebugScript.Quest.ERNEST_THE_CHICKEN),
        // ARCH_TUTORIAL_INSTRUCTION("Complete the Archaeology Tutorial quest. No special requirements needed.", DebugScript.Quest.ARCH_TUTORIAL),
        // CHRISTMAS_REUNION_INSTRUCTION("Have Christmas Village Teleport on Action Bar, Talk to Hunter NPC at the enterance of Citharede manual if you haven't done with before", DebugScript.Quest.CHRISTMAS_REUNION),
        // ITS_SNOW_BOTHER_INSTRUCTION("Have Christmas Village Teleport on Action Bar.  Might need to Select 5 NPCs to deliver presents to. - NPC are Sir Amik Varze, Bob, Brugsen Bursen, Doric and Reldo", DebugScript.Quest.ITS_SNOW_BOTHER),
        // DEAD_AND_BURIED_INSTRUCTION("Have Items in banks and armour equipped and some food.Puzzel required manual intervention.", DebugScript.Quest.DEAD_AND_BURIED),
        // ANCIENT_AWAKENING_INSTRUCTION("Have Armour and weapon equipped. Inventory fill with food before going to Ungael Site, 12 Waves are required manual intervention.", DebugScript.Quest.ANCIENT_AWAKENING),
        // BATTLE_OF_FORINTHRY_INSTRUCTION("Requires Grove Gabin Tier 1, Botanist's Workbench Tier 1. Have Armour and weapon equipped. Inventory fill with food before going to fight with Vorkath", DebugScript.Quest.BATTLE_OF_FORINTHRY),
        // REQUIEM_FOR_A_DRAGON_INSTRUCTION("Talking to Archivist and Zemouregal is required manual intervention. Resotring becon chat option with tree of balance is required manual intervention. Ritual required manual intervention.", DebugScript.Quest.REQUIEM_FOR_A_DRAGON),
        // MURDER_ON_THE_BORDER_INSTRUCTION("Requires Town Hall Tier 1, Command Centre Tier 1, Chapel Tier 1.  Talk to Rodney during second half of the quest", DebugScript.Quest.MURDER_ON_THE_BORDER),
        // IMP_CATCHER_INSTRUCTION("Race the imp at Air Ruins manual", DebugScript.Quest.IMP_CATCHER),
        // ICTHLARIN_INSTRUCTION("Piramid walk is required manual intervention\r\nHave Bag of salt or Pile of salt in inventory\r\nHave all items in inventory before starting", DebugScript.Quest.ICTHLARIN_LITTLE_HELPER),
        // IN_SEARCH_OF_THE_MYREQUE_INSTRUCTION("Make sure to overfill your druid pouch i recommened 40+ just in case. lots of ghasts hit you via nav", DebugScript.Quest.IN_SEARCH_OF_THE_MYREQUE),
        // IN_AID_OF_THE_MYREQUE_INSTRUCTION("Start quest with 5 food and 5 buckets in inventory. \r\nBuy STEEL Nails. Keep all required Items in Bank. \r\nMake sure you are in melee gear, or no gear so you have accuracy with the sickle. \r\nRequires manual input of enchanting rod with lvl 1 enchant \r\n Food may not be the same for filling the crate", DebugScript.Quest.IN_AID_OF_THE_MYREQUE),
        // PRIEST_IN_PERIL_INSTRUCTION("Have 25 pure essence and a bucket in your inventory, along with a weapon wielded. Will have to hand in 50 rune essence manually if F2P", DebugScript.Quest.PRIEST_IN_PERIL),
        // THE_DARKNESS_OF_HALLOWVALE_INSTRUCTION("Have Items in inventory", DebugScript.Quest.THE_DARKNESS_OF_HALLOWVALE),
        // LEGACY_OF_SEERGAZE_INSTRUCTION("Have Items in inventory. Burgh de Rott is required manual intervention. Maze required manual intervention", DebugScript.Quest.LEGACY_OF_SEERGAZE),
        // TEMPLE_OF_IKOV_INSTRUCTION("Have no items in equipment slot expect bow. Lit candle in inventory and need to equip Lucient pendant manaually as soon you recieved it - Siding with Lucien", DebugScript.Quest.TEMPLE_OF_IKOV),
        // TROLL_STRONGHOLD_INSTRUCTION("Have Climbing boots equipped", DebugScript.Quest.TROLL_STRONGHOLD),
        // FAMILY_CREST_INSTRUCTION("Have items in inventory. Flip the lever in witchhaven dungeon manually, and smelt the ore and craft the Ring and necklace manually. Fight Chronozon manually and pick up the crest", DebugScript.Quest.FAMILY_CREST),
        // THE_DIG_SITE_INSTRUCTION("Have items in inventory and Arch journal equipped for faster teleport", DebugScript.Quest.THE_DIG_SITE),
        // MISSING_MY_MUMMY_INSTRUCTION("Start Quest with Required Item. \r\nLighting scounce in order South, North,West, East. \r\n Collect 4 jars, use mummy hand on mummy no hand and then come to back to mummy room", DebugScript.Quest.MISSING_MY_MUMMY),
        // MOGRE_ACTIVITY_INSTRUCTION("Have Items in inventory, nettle tea, chocolate dust, buket of milk, buket of water and snape grass", DebugScript.Quest.MOGRE_ACTIVITY),
        // MAKING_HISTORY_INSTRUCTION(" Need to answer Daron's question manually ", DebugScript.Quest.MAKING_HISTORY),
        // GOBLIN_DIPLOMACY_INSTRUCTION("Have 3 armour and dyes in inventory", DebugScript.Quest.GOBLIN_DIPLOMACY),
        // DIAMOND_ROUGH_INSTRUCTION("Underground sundial required manual intervention and sometime talking to Ozan required manual intervention", DebugScript.Quest.DIAMOND_ROUGH);
    }
}