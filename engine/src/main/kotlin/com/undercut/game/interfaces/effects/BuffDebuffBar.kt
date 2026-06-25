package com.undercut.game.interfaces.effects

import com.undercut.game.Skill
import com.undercut.game.bootstrap.Bootstrap
import com.undercut.script.api.equipment
import com.undercut.script.api.getCurrentLevel
import com.undercut.script.api.getRealLevel
import com.undercut.script.api.varcs
import com.undercut.script.api.varps
import java.lang.Integer.bitCount

private fun getOpponentPrayerOverheadFlag(index: Int): Int { // script7703
    return if (varcs.getVar(4168) == 0) {
        when (index) {
            0 -> varps.getVarBit(1967)
            1 -> varps.getVarBit(1966)
            2 -> varps.getVarBit(1965)
            3 -> varps.getVarBit(2016)
            4 -> varps.getVarBit(2015)
            5 -> varps.getVarBit(2007)
            6 -> varps.getVarBit(2006)
            7 -> varps.getVarBit(2005)
            8 -> varps.getVarBit(53230)
            9 -> varps.getVarBit(53241)
            else -> 0
        }
    } else
        (varcs.getVar(4168) shr index) and 1
}

fun isEffectActive(effect: Effect): Boolean {
    if (getEffectTimeRemaining(effect) > 0) return true
    if (getEffectStacks(effect) > 0) return true
    return when(effect.structId) {
        /**
         * custom extensions
         */
        1000 -> getEffectTimeRemaining(Effect.VENGEFUL_GHOST) > 0 && varps.getVar(11021) != 0

        /**
         * Prayers
         */
        14541, 14542, 14543, 14540 -> varps.getVarBit(16739) != 0
        14545, 14546, 14547, 14544 -> varps.getVarBit(16740) != 0
        14549, 14550, 14551, 14548 -> varps.getVarBit(16741) != 0
        14572 -> varps.getVarBit(16742) != 0
        14573 -> varps.getVarBit(16743) != 0
        14575 -> varps.getVarBit(16744) != 0
        14576 -> varps.getVarBit(16745) != 0
        14577 -> varps.getVarBit(16746) != 0
        14578 -> varps.getVarBit(16747) != 0
        14580 -> varps.getVarBit(16748) != 0
        14581 -> varps.getVarBit(16749) != 0
        14582 -> varps.getVarBit(16750) != 0
        14553, 14554, 14555, 14552 -> varps.getVarBit(16751) != 0
        14561, 14562, 14563, 14560 -> varps.getVarBit(16752) != 0
        14557, 14558, 14559, 14556 -> varps.getVarBit(16753) != 0
        14565, 14566, 14567, 14564 -> varps.getVarBit(16754) != 0
        14579 -> varps.getVarBit(16755) != 0
        14568 -> varps.getVarBit(16756) != 0
        14569 -> varps.getVarBit(16757) != 0
        14574 -> varps.getVarBit(16758) != 0
        14570 -> varps.getVarBit(16759) != 0
        14571 -> varps.getVarBit(16760) != 0
        48361, 48362, 48363, 48360 -> varps.getVarBit(53271) != 0
        48365, 48366, 48367, 48364 -> varps.getVarBit(53272) != 0
        48368 -> varps.getVarBit(53273) != 0
        48369 -> varps.getVarBit(53274) != 0
        50077 -> varps.getVarBit(55729) != 0
        50228 -> varps.getVarBit(55986) != 0
        14583 -> varps.getVarBit(16761) != 0
        14584 -> varps.getVarBit(16762) != 0
        14585 -> varps.getVarBit(16763) != 0
        14586 -> varps.getVarBit(16786) != 0
        14587 -> varps.getVarBit(16764) != 0
        14588 -> varps.getVarBit(16785) != 0
        14591 -> varps.getVarBit(16787) != 0
        14590 -> varps.getVarBit(16788) != 0
        14589 -> varps.getVarBit(16765) != 0
        14592 -> varps.getVarBit(16766) != 0
        14593 -> varps.getVarBit(16767) != 0
        14594 -> varps.getVarBit(16768) != 0
        14595 -> varps.getVarBit(16769) != 0
        14596 -> varps.getVarBit(16770) != 0
        14597 -> varps.getVarBit(16771) != 0
        14598 -> varps.getVarBit(16772) != 0
        14599 -> varps.getVarBit(16781) != 0
        14600 -> varps.getVarBit(16773) != 0
        14601 -> varps.getVarBit(16782) != 0
        14602 -> varps.getVarBit(16774) != 0
        14603 -> varps.getVarBit(16775) != 0
        14604 -> varps.getVarBit(16776) != 0
        14605 -> varps.getVarBit(16777) != 0
        14606 -> varps.getVarBit(16778) != 0
        14607 -> varps.getVarBit(16779) != 0
        14608 -> varps.getVarBit(16780) != 0
        14609 -> varps.getVarBit(16784) != 0
        14610 -> varps.getVarBit(16783) != 0
        32272 -> varps.getVarBit(29065) != 0
        32273 -> varps.getVarBit(29066) != 0
        32274 -> varps.getVarBit(29067) != 0
        32275 -> varps.getVarBit(29068) != 0
        32276 -> varps.getVarBit(29069) != 0
        29241 -> varps.getVarBit(49330) != 0
        32278 -> varps.getVarBit(29071) != 0
        35360 -> varps.getVarBit(34866) != 0
        35361 -> varps.getVarBit(34867) != 0
        35362 -> varps.getVarBit(34868) != 0
        48370 -> varps.getVarBit(53275) != 0
        48371 -> varps.getVarBit(53276) != 0
        48373 -> varps.getVarBit(53277) != 0
        48374 -> varps.getVarBit(53278) != 0
        48375 -> varps.getVarBit(53279) != 0
        48376 -> varps.getVarBit(53280) != 0
        48378 -> varps.getVarBit(53281) != 0
        51666 -> varps.getVarBit(58052) != 0
        51665 -> varps.getVarBit(58053) != 0
        21176 -> getOpponentPrayerOverheadFlag(2) != 0
        21177 -> getOpponentPrayerOverheadFlag(1) != 0
        36801 -> getOpponentPrayerOverheadFlag(0) != 0

        44226 -> {
            when {
                varps.getVarBit(1984) == 1 || varps.getVarBit(1985) == 1 ||
                        varps.getVarBit(2017) == 1 || varps.getVarBit(34863) == 1 -> false
                varps.getVarBit(2038) == 1 -> true
                varps.getVarBit(2039) == 1 -> true
                varps.getVarBit(1999) == 1 -> true
                varps.getVarBit(2008) == 1 -> true
                varps.getVarBit(1959) == 1 -> true
                varps.getVarBit(1960) == 1 -> true
                varps.getVarBit(1961) == 1 -> true
                else -> false
            }
        }
        44229 -> {
            when {
                varps.getVarBit(1988) == 1 || varps.getVarBit(1997) == 1 || varps.getVarBit(34864) == 1 -> false
                varps.getVarBit(2042) == 1 -> true
                varps.getVarBit(2043) == 1 -> true
                varps.getVarBit(2000) == 1 -> true
                varps.getVarBit(2009) == 1 -> true
                varps.getVarBit(1971) == 1 -> true
                varps.getVarBit(1972) == 1 -> true
                varps.getVarBit(1973) == 1 -> true
                else -> false
            }
        }
        44231 -> {
            when {
                varps.getVarBit(1987) == 1 || varps.getVarBit(1996) == 1 || varps.getVarBit(34865) == 1 -> false
                varps.getVarBit(2046) == 1 -> true
                varps.getVarBit(2047) == 1 -> true
                varps.getVarBit(2001) == 1 -> true
                varps.getVarBit(2010) == 1 -> true
                varps.getVarBit(1974) == 1 -> true
                varps.getVarBit(1975) == 1 -> true
                varps.getVarBit(1976) == 1 -> true
                else -> false
            }
        }
        44228 -> {
            when {
                varps.getVarBit(1984) == 1 || varps.getVarBit(1985) == 1 ||
                        varps.getVarBit(1988) == 1 || varps.getVarBit(1987) == 1 ||
                        varps.getVarBit(2017) == 1 || varps.getVarBit(1997) == 1 ||
                        varps.getVarBit(1996) == 1 || varps.getVarBit(34865) == 1 ||
                        varps.getVarBit(34863) == 1 || varps.getVarBit(34864) == 1 -> false
                varps.getVarBit(2050) == 1 -> true
                varps.getVarBit(2051) == 1 -> true
                varps.getVarBit(1992) == 1 -> true
                varps.getVarBit(2011) == 1 -> true
                varps.getVarBit(1953) == 1 -> true
                varps.getVarBit(1954) == 1 -> true
                varps.getVarBit(1955) == 1 -> true
                else -> false
            }
        }
        44227 -> {
            when {
                varps.getVarBit(1984) == 1 || varps.getVarBit(1985) == 1 ||
                        varps.getVarBit(2017) == 1 || varps.getVarBit(34863) == 1 -> false
                varps.getVarBit(2040) == 1 -> true
                varps.getVarBit(2041) == 1 -> true
                varps.getVarBit(1989) == 1 -> true
                varps.getVarBit(1993) == 1 -> true
                varps.getVarBit(1956) == 1 -> true
                varps.getVarBit(1957) == 1 -> true
                varps.getVarBit(1958) == 1 -> true
                else -> false
            }
        }
        44230 -> {
            when {
                varps.getVarBit(1988) == 1 || varps.getVarBit(1997) == 1 || varps.getVarBit(34864) == 1 -> false
                varps.getVarBit(2044) == 1 -> true
                varps.getVarBit(2045) == 1 -> true
                varps.getVarBit(1990) == 1 -> true
                varps.getVarBit(1994) == 1 -> true
                varps.getVarBit(1977) == 1 -> true
                varps.getVarBit(1978) == 1 -> true
                varps.getVarBit(1979) == 1 -> true
                else -> false
            }
        }
        44232 -> {
            when {
                varps.getVarBit(1987) == 1 || varps.getVarBit(1996) == 1 || varps.getVarBit(34865) == 1 -> false
                varps.getVarBit(2048) == 1 -> true
                varps.getVarBit(2049) == 1 -> true
                varps.getVarBit(1991) == 1 -> true
                varps.getVarBit(1995) == 1 -> true
                varps.getVarBit(1980) == 1 -> true
                varps.getVarBit(1981) == 1 -> true
                varps.getVarBit(1982) == 1 -> true
                else -> false
            }
        }
        36802 -> getOpponentPrayerOverheadFlag(7) != 0
        36861 -> getOpponentPrayerOverheadFlag(6) != 0
        36862 -> getOpponentPrayerOverheadFlag(5) != 0
//        14606 -> getOpponentPrayerOverheadFlag(4) != 0
//        14607 -> getOpponentPrayerOverheadFlag(3) != 0

        14885 -> varps.getVarBit(2018) == 1
        14886 -> varps.getVarBit(2019) == 1
        14887 -> varps.getVarBit(2020) == 1
        14888 -> varps.getVarBit(2021) == 1
        14889 -> varps.getVarBit(2022) == 1
        14890 -> varps.getVarBit(2023) == 1
        14891 -> varps.getVarBit(2024) == 1
        14892 -> varps.getVarBit(2025) == 1
        14895 -> varps.getVarBit(2026) == 1
        14896 -> varps.getVarBit(2027) == 1
        14897 -> varps.getVarBit(2028) == 1
        14898 -> varps.getVarBit(2029) == 1
        14893 -> varps.getVarBit(2030) == 1
        14894 -> varps.getVarBit(2031) == 1
        14899 -> varps.getVarBit(2032) == 1
        14900 -> varps.getVarBit(2033) == 1
        14903 -> varps.getVarBit(2034) == 1
        14904 -> varps.getVarBit(2035) == 1
        14905 -> varps.getVarBit(2037) != 0
        14884 -> varps.getVarBit(1911) != 0

        14693 -> varps.getVarBit(2052) == 2
        14694 -> varps.getVarBit(2052) == 3
        14695 -> varps.getVarBit(2052) == 4

        14901 -> varps.getVarBit(2054) != 0
        14920 -> varps.getVarBit(2055) != 0
        19828 -> varps.getVarBit(18549) != 0
        23129 -> varps.getVarBit(20383) == 1
        28178 -> varps.getVarBit(22458) != 0
        28180 -> varps.getVarBit(22457) != 0
        24374 -> varps.getVarBit(23303) != 0
        29605 -> varps.getVarBit(25842) != 0
        29606 -> varps.getVarBit(25843) != 0
        29607 -> varps.getVarBit(25844) != 0
        14865 -> varps.getVarBit(26431) != 0
        31986 -> varps.getVarBit(28637) != 0
        31985 -> varps.getVarBit(28638) != 0
        31982 -> varps.getVarBit(28639) != 0
        30956 -> varps.getVarBit(29797) != 0
        33650 -> varps.getVarBit(32613) != 0
        33658 -> varps.getVarBit(32614) != 0
        34984 -> varps.getVarBit(34308) != 0
        34985 -> varps.getVarBit(34309) != 0
        34986 -> varps.getVarBit(34310) != 0
//        35360 -> varps.getVarBit(34863) != 0
//        35362 -> varps.getVarBit(34865) != 0
//        35361 -> varps.getVarBit(34864) != 0
        35798 -> varps.getVarBit(35308) != 0
        37401 -> varps.getVarBit(35397) != 0
        37403 -> varps.getVarBit(35399) != 0
        37402 -> varps.getVarBit(35398) != 0
        1489 -> minOf(1, varps.getVarBit(55117)) != 0
        37659 -> varps.getVarBit(36804) != 0
        39040 -> varps.getVarBit(38913) != 0
        39041 -> varps.getVarBit(38914) != 0
        39140 -> varps.getVarBit(38966) > 0
        39242 -> varps.getVarBit(39314) != 0
        39243 -> varps.getVarBit(39315) != 0
        39244 -> varps.getVarBit(39316) != 0
        39784 -> varps.getVarBit(40076) != 0
        40936 -> varps.getVarBit(41441) != 0
        41143 -> varps.getVarBit(41573) != 0
        41144 -> varps.getVarBit(41574) != 0
        4550 -> varps.getVarBit(43412) > 0
        6860 -> varps.getVarBit(44123) != 0
        30521 -> varps.getVarBit(44230) != 0
        35992 -> varps.getVarBit(46014) != 0
        35993 -> varps.getVarBit(46015) != 0
        36000 -> varps.getVarBit(46016) != 0
        36001 -> varps.getVarBit(46017) != 0
        35898 -> varps.getVarBit(46018) != 0
        44875 -> varps.getVarBit(48028) != 0
        45047 -> varps.getVarBit(48686) != 0
        45045 -> varps.getVarBit(48685) != 0
        44912 -> varps.getVarBit(49448) != 0
        44946 -> varps.getVarBit(49449) != 0
        45400 -> varps.getVarBit(49723) != 0
        45605 -> varps.getVarBit(41541) != 0
        45117 -> varps.getVarBit(21556) != 0
        45797 -> varps.getVarBit(50328) != 0
        51672 -> varps.getVarBit(58068) != 0 || varps.getVarBit(58067) != 0
        45567 -> varps.getVarBit(50329) != 0
        3694 -> varps.getVarBit(51063) != 0
        43673 -> varps.getVarBit(4332) > 0
        41807 -> varps.getVarBit(51434) != 0
        46211 -> varps.getVarBit(51435) != 0
        41808 -> varps.getVarBit(51436) != 0
        46279 -> varps.getVarBit(51431) != 0
        46272 -> varps.getVarBit(51432) != 0
        41810 -> varps.getVarBit(51437) != 0
        41811 -> varps.getVarBit(51438) != 0
        46377 -> varps.getVarBit(51704) != 0
        47202 -> varps.getVarBit(52819) != 0

        48286 -> {
            when {
                varps.getVarBit(53229) == 1 || varps.getVarBit(53239) == 1 || varps.getVarBit(53240) == 1 -> false
                varps.getVarBit(53232) == 1 -> true
                varps.getVarBit(53231) == 1 -> true
                varps.getVarBit(53233) == 1 -> true
                varps.getVarBit(53234) == 1 -> true
                varps.getVarBit(53223) == 1 -> true
                varps.getVarBit(53224) == 1 -> true
                varps.getVarBit(53225) == 1 -> true
                else -> false
            }
        }
//        48370 -> {
//            when {
//                varps.getVarBit(53229) == 1 || varps.getVarBit(53239) == 1 || varps.getVarBit(53240) == 1 -> false
//                varps.getVarBit(53233) == 1 -> true
//                else -> false
//            }
//        }
//        48373 -> {
//            when {
//                varps.getVarBit(53229) == 1 || varps.getVarBit(53239) == 1 || varps.getVarBit(53240) == 1 -> false
//                varps.getVarBit(53234) == 1 -> true
//                else -> false
//            }
//        }
//        48361 -> {
//            when {
//                varps.getVarBit(53229) == 1 || varps.getVarBit(53239) == 1 || varps.getVarBit(53240) == 1 -> false
//                varps.getVarBit(53223) == 1 -> true
//                else -> false
//            }
//        }
//        48362 -> {
//            when {
//                varps.getVarBit(53229) == 1 || varps.getVarBit(53239) == 1 || varps.getVarBit(53240) == 1 -> false
//                varps.getVarBit(53224) == 1 -> true
//                else -> false
//            }
//        }
//        48363 -> {
//            when {
//                varps.getVarBit(53229) == 1 || varps.getVarBit(53239) == 1 || varps.getVarBit(53240) == 1 -> false
//                varps.getVarBit(53225) == 1 -> true
//                else -> false
//            }
//        }

        48287 -> {
            when {
                varps.getVarBit(53229) == 1 || varps.getVarBit(53239) == 1 || varps.getVarBit(53240) == 1 -> false
                varps.getVarBit(53236) == 1 -> true
                varps.getVarBit(53235) == 1 -> true
                varps.getVarBit(53237) == 1 -> true
                varps.getVarBit(53238) == 1 -> true
                varps.getVarBit(53226) == 1 -> true
                varps.getVarBit(53227) == 1 -> true
                varps.getVarBit(53228) == 1 -> true
                else -> false
            }
        }
//        48371 -> {
//            when {
//                varps.getVarBit(53229) == 1 || varps.getVarBit(53239) == 1 || varps.getVarBit(53240) == 1 -> false
//                varps.getVarBit(53237) == 1 -> true
//                else -> false
//            }
//        }
//        48374 -> {
//            when {
//                varps.getVarBit(53229) == 1 || varps.getVarBit(53239) == 1 || varps.getVarBit(53240) == 1 -> false
//                varps.getVarBit(53238) == 1 -> true
//                else -> false
//            }
//        }
//        48365 -> {
//            when {
//                varps.getVarBit(53229) == 1 || varps.getVarBit(53239) == 1 || varps.getVarBit(53240) == 1 -> false
//                varps.getVarBit(53226) == 1 -> true
//                else -> false
//            }
//        }
//        48366 -> {
//            when {
//                varps.getVarBit(53229) == 1 || varps.getVarBit(53239) == 1 || varps.getVarBit(53240) == 1 -> false
//                varps.getVarBit(53227) == 1 -> true
//                else -> false
//            }
//        }
//        48367 -> {
//            when {
//                varps.getVarBit(53229) == 1 || varps.getVarBit(53239) == 1 || varps.getVarBit(53240) == 1 -> false
//                varps.getVarBit(53228) == 1 -> true
//                else -> false
//            }
//        }

//        48368 -> varps.getVarBit(53229) != 0
        48377 -> getOpponentPrayerOverheadFlag(8) != 0
//        48375 -> varps.getVarBit(53239) != 0
//        48376 -> varps.getVarBit(53240) != 0
//        48378 -> getOpponentPrayerOverheadFlag(9) != 0
        48288 -> varps.getVarBit(53244) != 0

        48289 -> varps.getVarBit(53242) == 1
        48290 -> varps.getVarBit(53243) == 1

        48338 -> varps.getVarBit(53245) != 0
        48344 -> varps.getVarBit(53246) != 0
        48345 -> varps.getVarBit(53247) != 0
        48346 -> varps.getVarBit(53248) != 0
        48283 -> varps.getVarBit(53249) != 0
        48284 -> varps.getVarBit(53250) != 0
        48285 -> varps.getVarBit(53251) != 0
        49074 -> varps.getVarBit(54672) != 0
        49071 -> minOf(1, varps.getVar(11534)) != 0
        49552 -> varps.getVarBit(55118) != 0
//        50077 -> varps.getVarBit(55725) != 0
        50067 -> varps.getVarBit(55726) != 0
        50069 -> varps.getVarBit(55727) != 0
        50068 -> varps.getVarBit(55728) != 0
//        50228 -> varps.getVarBit(55985) != 0
        50696 -> varps.getVarBit(56288) != 0

        else -> false
    }
}

private fun getEffectExpiryCycle(structId: Int): Int { // script11073
    return when (structId) {
        14710 -> varcs.getVar(3736)
        14711 -> varcs.getVar(3733)
        14713 -> varcs.getVar(3741)
        14714 -> varcs.getVar(3740)
        14716 -> varcs.getVar(3742)
        14717 -> varcs.getVar(5977)
        12009 -> varcs.getVar(6675)
        14718 -> varcs.getVar(5965)
        14719 -> varcs.getVar(3735)
        14720 -> varcs.getVar(3737)
        14721 -> varcs.getVar(3734)
        45045 -> varcs.getVar(6868)
        45340 -> varcs.getVar(6953)
        14701, 14706, 14708, 14731, 14673, 14704, 28180, 19343, 44238, 45450, 47055 -> varcs.getVar(3746)
        14707 -> varcs.getVar(3731)
        46279 -> varcs.getVar(7061)
        14734 -> varcs.getVar(3743)
        14709 -> varcs.getVar(6643)
        31326 -> varcs.getVar(4788)
        31330 -> varcs.getVar(4790)
        31331 -> varcs.getVar(4789)
        31333 -> varcs.getVar(4791)
        31328 -> varcs.getVar(4792)
        31332 -> varcs.getVar(4793)
        45323 -> varcs.getVar(6921)
        45324 -> varcs.getVar(6922)
        45325 -> varcs.getVar(6923)
        45326 -> varcs.getVar(6924)
        45327 -> varcs.getVar(6925)
        45328 -> varcs.getVar(6926)
        45329 -> varcs.getVar(6927)
        45330 -> varcs.getVar(6928)
        45331 -> varcs.getVar(6929)
        31989 -> varcs.getVar(4982)
        31990 -> varcs.getVar(4983)
        31991 -> varcs.getVar(4984)
        31992 -> varcs.getVar(4985)
        32116 -> varcs.getVar(4989)
        32257 -> varcs.getVar(4996)
        32403 -> varcs.getVar(5094)
        30869 -> varcs.getVar(5112)
        33047 -> varcs.getVar(5127)
        39803 -> varcs.getVar(7049)
        21215 -> varcs.getVar(5152)
        33215 -> varcs.getVar(5184)
        33291 -> varcs.getVar(5186)
        33383 -> varcs.getVar(5189)
        33984 -> varcs.getVar(5837)
        28179 -> varcs.getVar(4184)
        28639 -> varcs.getVar(4243)
        28638 -> varcs.getVar(4244)
        28785 -> varcs.getVar(4261)
        28786 -> varcs.getVar(4262)
        24334 -> varcs.getVar(4490)
        29171 -> varcs.getVar(4621)
        29172 -> varcs.getVar(4623)
        29173 -> varcs.getVar(4622)
        29174, 29170 -> varcs.getVar(4620)
        29594 -> varcs.getVar(4627)
        29595 -> varcs.getVar(4628)
        29596 -> varcs.getVar(4629)
        29597 -> varcs.getVar(4630)
        29598 -> varcs.getVar(4631)
        29599 -> varcs.getVar(4632)
        29600 -> varcs.getVar(4633)
        29601 -> varcs.getVar(4634)
        29602 -> varcs.getVar(4635)
        29608 -> varcs.getVar(4636)
        29609 -> varcs.getVar(4637)
        29610 -> varcs.getVar(4638)
        29611 -> varcs.getVar(4639)
        29612 -> varcs.getVar(4640)
        29613 -> varcs.getVar(4641)
        29614 -> varcs.getVar(4642)
        29615 -> varcs.getVar(4643)
        29616 -> varcs.getVar(4644)
        29340 -> varcs.getVar(4625)
        30471 -> varcs.getVar(4720)
        30472 -> varcs.getVar(4721)
        30925 -> varcs.getVar(4744)
        14684, 40936, 14666, 14670 -> varcs.getVar(3746)
        39030 -> varcs.getVar(6353)
        14883 -> varcs.getVar(3748)
        35750 -> varcs.getVar(5968)
        35751 -> varcs.getVar(5969)
        35806 -> varcs.getVar(5970)
        35807 -> varcs.getVar(5971)
        33544, 33782 -> varcs.getVar(5972)
        35809 -> varcs.getVar(5973)
        35810 -> varcs.getVar(5974)
        35811 -> varcs.getVar(5975)
        35812 -> varcs.getVar(5976)
        35815 -> varcs.getVar(5978)
        35822 -> varcs.getVar(5979)
        35818 -> varcs.getVar(5980)
        35819 -> varcs.getVar(5981)
        35820 -> varcs.getVar(5982)
        35821 -> varcs.getVar(5983)
        35823 -> varcs.getVar(5984)
        35824 -> varcs.getVar(5985)
        35825 -> varcs.getVar(5986)
        35827 -> varcs.getVar(5987)
        35828 -> varcs.getVar(5988)
        1416 -> varcs.getVar(6028)
        1417 -> varcs.getVar(6029)
        1418 -> varcs.getVar(6030)
        37132 -> varcs.getVar(6031)
        1400 -> varcs.getVar(6034)
        33384 -> varcs.getVar(5190)
        33490 -> varcs.getVar(5210)
        33491 -> varcs.getVar(5211)
        33650 -> varcs.getVar(5492)
        33658 -> varcs.getVar(5493)
        33689 -> varcs.getVar(5494)
        33795 -> varcs.getVar(5832)
        33907 -> varcs.getVar(5835)
        33985 -> varcs.getVar(5838)
        33986 -> varcs.getVar(5839)
        34188 -> varcs.getVar(5868)
        34189 -> varcs.getVar(5869)
        34169 -> varcs.getVar(5870)
        34878 -> varcs.getVar(5878)
        34984 -> varcs.getVar(5883)
        34985 -> varcs.getVar(5884)
        34986 -> varcs.getVar(5885)
        35079 -> varcs.getVar(5908)
        35080 -> varcs.getVar(5909)
        34747 -> varcs.getVar(5918)
        35314 -> varcs.getVar(5919)
        34527 -> varcs.getVar(5945)
        35781 -> varcs.getVar(5946)
        35748 -> varcs.getVar(5966)
        35749 -> varcs.getVar(5967)
        1408 -> varcs.getVar(6033)
        37133 -> varcs.getVar(6032)
        23173 -> varcs.getVar(6067)
        37208 -> varcs.getVar(6613)
        37214 -> varcs.getVar(6614)
        37215 -> varcs.getVar(6615)
        1625 -> varcs.getVar(6071)
        1626 -> varcs.getVar(6076)
        37420 -> varcs.getVar(6072)
        37421 -> varcs.getVar(6073)
        37423 -> varcs.getVar(6075)
        37424 -> varcs.getVar(6077)
        37659 -> varcs.getVar(6256)
        6899 -> varcs.getVar(6295)
        47460 -> varcs.getVar(7155)
        39027 -> varcs.getVar(6350)
        39028 -> varcs.getVar(6351)
        39031 -> varcs.getVar(6354)
        39032 -> varcs.getVar(6355)
        39033 -> varcs.getVar(6356)
        39034 -> varcs.getVar(6357)
        39035 -> varcs.getVar(6358)
        39036 -> varcs.getVar(6359)
        39039 -> varcs.getVar(6360)
        14899, 14900 -> varcs.getVar(4681)
        29603, 14903 -> varcs.getVar(3750)
        14904 -> varcs.getVar(3751)
        14905, 29604 -> varcs.getVar(3747)
        44875 -> varcs.getVar(6824)
        14884 -> varcs.getVar(3749)
        14901 -> varcs.getVar(4191)
        19253 -> varcs.getVar(3739)
        19252 -> varcs.getVar(3738)
        19251, 46276 -> varcs.getVar(3745)
        19254, 46275 -> varcs.getVar(3744)
        23129, 29606, 36922 -> varcs.getVar(3732)
        24188 -> varcs.getVar(3902)
        25028 -> varcs.getVar(3901)
        24189 -> varcs.getVar(3903)
        24190 -> varcs.getVar(3904)
        25687 -> varcs.getVar(3944)
        25688 -> varcs.getVar(3945)
        920 -> varcs.getVar(8258)
        40606 -> varcs.getVar(6514)
        37216 -> varcs.getVar(6616)
        37217 -> varcs.getVar(6617)
        37218 -> varcs.getVar(6618)
        37219 -> varcs.getVar(6619)
        4549 -> varcs.getVar(6528)
        39391 -> varcs.getVar(6377)
        39392 -> varcs.getVar(6378)
        39437 -> varcs.getVar(6380)
        39438 -> varcs.getVar(6381)
        39439 -> varcs.getVar(6382)
        39440 -> varcs.getVar(6383)
        40032 -> varcs.getVar(6411)
        40033 -> varcs.getVar(6412)
        40034 -> varcs.getVar(6413)
        40035 -> varcs.getVar(6414)
        40036 -> varcs.getVar(6415)
        40237 -> varcs.getVar(6435)
        40899 -> varcs.getVar(6482)
        49534 -> varcs.getVar(7402)
        40939 -> varcs.getVar(6486)
        41145 -> varcs.getVar(6494)
        36919 -> varcs.getVar(6729)
        41146 -> varcs.getVar(6495)
        41147 -> varcs.getVar(6496)
        41148 -> varcs.getVar(6497)
        41887 -> varcs.getVar(6509)
        41888 -> varcs.getVar(6510)
        40604 -> varcs.getVar(6512)
        40605 -> varcs.getVar(6513)
        27609 -> varcs.getVar(4117)
        6938 -> varcs.getVar(6543)
        6939 -> varcs.getVar(6544)
        6940 -> varcs.getVar(6545)
        6941 -> varcs.getVar(6546)
        6942 -> varcs.getVar(6547)
        6943 -> varcs.getVar(6548)
        6944 -> varcs.getVar(6549)
        6945 -> varcs.getVar(6550)
        6946 -> varcs.getVar(6551)
        6947 -> varcs.getVar(6552)
        6948 -> varcs.getVar(6553)
        6949 -> varcs.getVar(6554)
        6950 -> varcs.getVar(6555)
        6951 -> varcs.getVar(6556)
        6952 -> varcs.getVar(6557)
        6953 -> varcs.getVar(6558)
        6954 -> varcs.getVar(6559)
        6955 -> varcs.getVar(6560)
        6957 -> varcs.getVar(6561)
        4286 -> varcs.getVar(6566)
        24374 -> varcs.getVar(6644)
        39029 -> varcs.getVar(6352)
        43721 -> varcs.getVar(6645)
        14667 -> varcs.getVar(6646)
        14674 -> varcs.getVar(6647)
        11652 -> varcs.getVar(6573)
        30522 -> varcs.getVar(6581)
        30758 -> varcs.getVar(6582)
        30759 -> varcs.getVar(6583)
        30821 -> varcs.getVar(6584)
        30828 -> varcs.getVar(6585)
        30964 -> varcs.getVar(6586)
        31386 -> varcs.getVar(6587)
        31562 -> varcs.getVar(6588)
        31918 -> varcs.getVar(6589)
        31919 -> varcs.getVar(6590)
        37206 -> varcs.getVar(6594)
        37207 -> varcs.getVar(6596)
        34338 -> varcs.getVar(6725)
        36920 -> varcs.getVar(6730)
        38070 -> varcs.getVar(6732)
        38071 -> varcs.getVar(6733)
        38072 -> varcs.getVar(6734)
        11602 -> varcs.getVar(6795)
        38074 -> varcs.getVar(6736)
        38075 -> varcs.getVar(6737)
        44892 -> varcs.getVar(6825)
        36921 -> varcs.getVar(6731)
        36923 -> varcs.getVar(6738)
        35946 -> varcs.getVar(6777)
        35947 -> varcs.getVar(6778)
        35950 -> varcs.getVar(6779)
        35951 -> varcs.getVar(6780)
        29046 -> varcs.getVar(8260)
        29047 -> varcs.getVar(8261)
        29048 -> varcs.getVar(8262)
        29049 -> varcs.getVar(8263)
        35968 -> varcs.getVar(6781)
        35990 -> varcs.getVar(6782)
        11599 -> varcs.getVar(6794)
        44596 -> varcs.getVar(6797)
        44340 -> varcs.getVar(6802)
        44341 -> varcs.getVar(6803)
        44342 -> varcs.getVar(6804)
        44343 -> varcs.getVar(6805)
        44344 -> varcs.getVar(6806)
        44428 -> varcs.getVar(6807)
        44429 -> varcs.getVar(6808)
        44430 -> varcs.getVar(6809)
        44431 -> varcs.getVar(6810)
        44432 -> varcs.getVar(6811)
        44433 -> varcs.getVar(6812)
        44434 -> varcs.getVar(6813)
        44698 -> varcs.getVar(6814)
        44705 -> varcs.getVar(6815)
        44853 -> varcs.getVar(6819)
        44854 -> varcs.getVar(6820)
        44877 -> varcs.getVar(6828)
        44878 -> varcs.getVar(6827)
        44879 -> varcs.getVar(6826)
        44793 -> varcs.getVar(6862)
        44794 -> varcs.getVar(6863)
        45036 -> varcs.getVar(6869)
        45168, 44062, 625 -> varcs.getVar(6872)
        45167 -> varcs.getVar(6871)
        44996 -> varcs.getVar(6898)
        44997 -> varcs.getVar(6899)
        44998 -> varcs.getVar(6900)
        44999 -> varcs.getVar(6901)
        32590 -> varcs.getVar(6902)
        29217 -> varcs.getVar(6905)
        4574 -> varcs.getVar(6904)
        45116 -> varcs.getVar(6907)
        44040 -> varcs.getVar(6912)
        46309, 46308 -> varcs.getVar(7122)
        44066 -> varcs.getVar(6913)
        44068 -> varcs.getVar(6914)
        44820 -> varcs.getVar(6915)
        44898 -> varcs.getVar(6916)
        45399 -> varcs.getVar(6956)
        45447 -> varcs.getVar(6958)
        45401 -> varcs.getVar(6959)
        45402 -> varcs.getVar(6960)
        45448 -> varcs.getVar(6973)
        45275 -> varcs.getVar(6978)
        45357 -> varcs.getVar(6977)
        45383 -> varcs.getVar(6979)
        45539 -> varcs.getVar(6980)
        45692 -> varcs.getVar(6981)
        45635 -> varcs.getVar(6982)
        45693 -> varcs.getVar(6983)
        44876 -> varcs.getVar(6984)
        35940 -> varcs.getVar(6985)
        45559 -> varcs.getVar(6993)
        45800, 28927 -> varcs.getVar(6992)
        45797 -> varcs.getVar(6994)
        46029 -> varcs.getVar(7041)
        46030 -> varcs.getVar(7042)
        46031 -> varcs.getVar(7043)
        46032 -> varcs.getVar(7044)
        46033 -> varcs.getVar(7045)
        46034 -> varcs.getVar(7046)
        43673 -> varcs.getVar(7052)
        43678 -> varcs.getVar(7053)
        43682 -> varcs.getVar(7054)
        46210 -> varcs.getVar(7062)
        46196 -> varcs.getVar(7063)
        46272 -> varcs.getVar(7066)
        46273 -> varcs.getVar(7067)
        41800 -> varcs.getVar(7064)
        41805 -> varcs.getVar(7065)
        46213 -> varcs.getVar(7068)
        49551 -> varcs.getVar(7069)
        47053 -> varcs.getVar(7091)
        47054 -> varcs.getVar(7092)
        47182 -> varcs.getVar(7112)
        46302 -> varcs.getVar(7117)
        47454 -> varcs.getVar(7120)
        47455 -> varcs.getVar(7121)
        47203 -> varcs.getVar(7123)
        47831, 47832 -> varcs.getVar(7124)
        49096 -> varcs.getVar(7352)
        49097 -> varcs.getVar(7351)
        48335 -> varcs.getVar(7231)
        48336 -> varcs.getVar(7236)
        48337 -> varcs.getVar(7241)
        32349 -> varcs.getVar(7795)
        48309 -> varcs.getVar(3746)
        48334 -> varcs.getVar(7246)
        48339 -> varcs.getVar(7265)
        48341 -> varcs.getVar(7274)
        48342 -> varcs.getVar(7277)
        48343 -> varcs.getVar(7280)
        48347 -> varcs.getVar(7283)
        49073 -> varcs.getVar(7349)
        48351 -> varcs.getVar(7288)
        39622 -> varcs.getVar(7338)
        48348 -> varcs.getVar(7285)
        48349 -> varcs.getVar(7287)
        48291 -> varps.getVar(11309)
        48283 -> varcs.getVar(7289)
        48284 -> varcs.getVar(7290)
        48285 -> varcs.getVar(7291)
        48844 -> varcs.getVar(7341)
        48845 -> varcs.getVar(7342)
        48846 -> varcs.getVar(7343)
        48847 -> varcs.getVar(7344)
        48848 -> varcs.getVar(7345)
        48849 -> varcs.getVar(7346)
        49070 -> varcs.getVar(7350)
        48878 -> varcs.getVar(7357)
        49535 -> varcs.getVar(7406)
        49536 -> varcs.getVar(7407)
        49537 -> varcs.getVar(7408)
        51667 -> varcs.getVar(8301)
        49540 -> varcs.getVar(7409)
        49538 -> varcs.getVar(7410)
        49541 -> varcs.getVar(7412)
        49563 -> varcs.getVar(7403)
        49562 -> varcs.getVar(7404)
        49913 -> varcs.getVar(7771)
        50084 -> varcs.getVar(7783)
        50085 -> varcs.getVar(7785)
        50069 -> varcs.getVar(7784)
        50070 -> varcs.getVar(7782)
        50065 -> varcs.getVar(7786)
        50064 -> varcs.getVar(7787)
        50067 -> varcs.getVar(7788)
        50068 -> varcs.getVar(7789)
        50228 -> varcs.getVar(7797)
        50241 -> varcs.getVar(7798)
        50246 -> varcs.getVar(7799)
        50247 -> varcs.getVar(7800)
        50248 -> varcs.getVar(7801)
        29054 -> varcs.getVar(8259)
        51129 -> varcs.getVar(8282)
        51130 -> varcs.getVar(8283)
        51272 -> varcs.getVar(8294)
        51665 -> varcs.getVar(8303)
        51666 -> varcs.getVar(8302)
        52782, 52785, 52790 -> varcs.getVar(3746)
        51841 -> varcs.getVar(8316)
        51848 -> varcs.getVar(8317)
        51842 -> varcs.getVar(8318)
        51843 -> varcs.getVar(8319)
        51844 -> varcs.getVar(8320)
        51845 -> varcs.getVar(8321)
        51856 -> varcs.getVar(8322)
        51846 -> varcs.getVar(8323)
        52061 -> varcs.getVar(8324)
        52062 -> varcs.getVar(8325)
        52065 -> varcs.getVar(8326)
        52063 -> varcs.getVar(8327)
        50083 -> varcs.getVar(8328)
        52080 -> varcs.getVar(8330)
        52238 -> varcs.getVar(8341)
        52239 -> varcs.getVar(8342)
        52240 -> varcs.getVar(8343)
        52319 -> varcs.getVar(8344)
        52318 -> varcs.getVar(8345)
        52658 -> varcs.getVar(8370)
        52792 -> varcs.getVar(8386)
        52793 -> varcs.getVar(8387)
        52802 -> varcs.getVar(8388)
        52801 -> varcs.getVar(8389)
        52778 -> varcs.getVar(8394)
        52779 -> varcs.getVar(8395)
        48291, 52776 -> varcs.getVar(8396)
        14662 -> varcs.getVar(8399)
        53002 -> varcs.getVar(8400)
        53033, 53034, 53035, 53036, 53037, 53038 -> varcs.getVar(8401)
        53039 -> varcs.getVar(8402)
        53040 -> varcs.getVar(8403)
        53041 -> varcs.getVar(8404)
        53003 -> varcs.getVar(8405)
        45563 -> varcs.getVar(8406)
        53077 -> varcs.getVar(8407)
        53078 -> varcs.getVar(8408)
        2907 -> varcs.getVar(8410)
        else -> 0
    }
}

fun isEffectActiveOnOpponent(effect: Effect): Boolean {
    return when (effect.structId) {
        51666 -> varps.getVarBit(58052) == 1
        14683 -> varps.getVarBit(1913) == 1
        14687 -> varps.getVarBit(1914) == 1
        14690 -> varps.getVarBit(1915) == 1
        14710 -> varps.getVarBit(1916) == 1
        14711 -> varps.getVarBit(1917) == 1
        14713 -> varps.getVarBit(1919) == 1
        14714 -> varps.getVarBit(1920) == 1
        14716 -> varps.getVarBit(1921) == 1
        14717 -> varps.getVarBit(1922) == 1
        14718 -> varps.getVarBit(1923) == 1
        14719 -> varps.getVarBit(1924) == 1
        14720 -> varps.getVarBit(1925) == 1
        14721 -> varps.getVarBit(1926) == 1
        44244 -> varps.getVarBit(1927) == 1
        14706 -> varps.getVarBit(1929) == 1
        14707 -> varps.getVarBit(1930) == 1
        14708 -> varps.getVarBit(1931) == 1
        51665 -> varps.getVarBit(58053) == 1
        14729 -> varps.getVarBit(1933) == 1
        14732 -> varps.getVarBit(1934) == 1
        14734 -> varps.getVarBit(1935) == 1
        14731 -> varps.getVarBit(2036) == 1
        14739 -> varps.getVarBit(1936) == 1
        14745 -> varps.getVarBit(1937) == 1
        14749 -> varps.getVarBit(1938) == 1
        14784 -> varps.getVarBit(1939) == 1
        14787 -> varps.getVarBit(1940) == 1
        14792 -> varps.getVarBit(1941) == 1
        14667 -> varps.getVarBit(1943) == 1
        14672 -> varps.getVarBit(1944) == 1
        14673 -> varps.getVarBit(1945) == 1
        14674 -> varps.getVarBit(1946) == 1
        14712 -> varps.getVarBit(1918) == 1
        14704 -> varps.getVarBit(1947) == 1
        14666 -> varps.getVarBit(1949) == 1
        14670 -> varps.getVarBit(1950) == 1
        39030 -> varps.getVarBit(1951) == 1
        14883 -> varps.getVarBit(1910) == 1
        14691 -> varps.getVarBit(1952) == 1
        14572 -> varps.getVarBit(1962) == 1
        14573 -> varps.getVarBit(1963) == 1
        14575 -> varps.getVarBit(1964) == 1
        14580 -> varps.getVarBit(1968) == 1
        14581 -> varps.getVarBit(1969) == 1
        14582 -> varps.getVarBit(1970) == 1
        14579 -> varps.getVarBit(1983) == 1
        14568 -> varps.getVarBit(1984) == 1
        14569 -> varps.getVarBit(1985) == 1
        14574 -> varps.getVarBit(1986) == 1
        14570 -> varps.getVarBit(1987) == 1
        14571 -> varps.getVarBit(1988) == 1
        14608 -> varps.getVarBit(2017) == 1
        14609 -> varps.getVarBit(1996) == 1
        14610 -> varps.getVarBit(1997) == 1
        14583 -> varps.getVarBit(1998) == 1
        14589 -> varps.getVarBit(2002) == 1
        14592 -> varps.getVarBit(2003) == 1
        14593 -> varps.getVarBit(2004) == 1
        14604 -> varps.getVarBit(2013) == 1
        14605 -> varps.getVarBit(2014) == 1
        14885 -> varps.getVarBit(2018) == 1
        14886 -> varps.getVarBit(2019) == 1
        14887 -> varps.getVarBit(2020) == 1
        14888 -> varps.getVarBit(2021) == 1
        14889 -> varps.getVarBit(2022) == 1
        14890 -> varps.getVarBit(2023) == 1
        14891 -> varps.getVarBit(2024) == 1
        14892 -> varps.getVarBit(2025) == 1
        14895 -> varps.getVarBit(2026) == 1
        14896 -> varps.getVarBit(2027) == 1
        14897 -> varps.getVarBit(2028) == 1
        14898 -> varps.getVarBit(2029) == 1
        14893 -> varps.getVarBit(2030) == 1
        14894 -> varps.getVarBit(2031) == 1
        14899 -> varps.getVarBit(2032) == 1
        14900 -> varps.getVarBit(2033) == 1
        14903 -> varps.getVarBit(2034) == 1
        14904 -> varps.getVarBit(2035) == 1
        14905 -> varps.getVarBit(2037) == 1
        14884 -> varps.getVarBit(1911) == 1
        14693 -> varps.getVarBit(2052) == 2
        14694 -> varps.getVarBit(2052) == 3
        14695 -> varps.getVarBit(2052) == 4
        14901 -> varps.getVarBit(2054) == 1
        14920 -> varps.getVarBit(2055) == 1
        19828 -> varps.getVarBit(18549) == 1
        23129 -> varps.getVarBit(20383) == 1
        28178 -> varps.getVarBit(22458) == 1
        28180 -> varps.getVarBit(22457) == 1
        24374 -> varps.getVarBit(23303) == 1
        29605 -> varps.getVarBit(25842) == 1
        29606 -> varps.getVarBit(25843) == 1
        29607 -> varps.getVarBit(25844) == 1
        14865 -> varps.getVarBit(26431) == 1
        31986 -> varps.getVarBit(28637) == 1
        31985 -> varps.getVarBit(28638) == 1
        31982 -> varps.getVarBit(28639) == 1
        30956 -> varps.getVarBit(29797) == 1
        33650 -> varps.getVarBit(32613) == 1
        33658 -> varps.getVarBit(32614) == 1
        34984 -> varps.getVarBit(34308) == 1
        34985 -> varps.getVarBit(34309) == 1
        34986 -> varps.getVarBit(34310) == 1
        35360 -> varps.getVarBit(34863) == 1
        35362 -> varps.getVarBit(34865) == 1
        35361 -> varps.getVarBit(34864) == 1
        35798 -> varps.getVarBit(35308) == 1
        37401 -> varps.getVarBit(35397) == 1
        37403 -> varps.getVarBit(35399) == 1
        37402 -> varps.getVarBit(35398) == 1
        1489 -> varps.getVarBit(55117) >= 1
        37659 -> varps.getVarBit(36804) == 1
        39040 -> varps.getVarBit(38913) == 1
        39041 -> varps.getVarBit(38914) == 1
        39140 -> varps.getVarBit(38966) > 0
        39242 -> varps.getVarBit(39314) == 1
        39243 -> varps.getVarBit(39315) == 1
        39244 -> varps.getVarBit(39316) == 1
        39784 -> varps.getVarBit(40076) == 1
        14684 -> varps.getVarBit(41440) == 1
        40936 -> varps.getVarBit(41441) == 1
        14701 -> varps.getVarBit(41445) == 1
        41143 -> varps.getVarBit(41573) == 1
        41144 -> varps.getVarBit(41574) == 1
        4550 -> varps.getVarBit(43412) > 0
        30521 -> varps.getVarBit(44230) == 1
        35992 -> varps.getVarBit(46014) == 1
        35993 -> varps.getVarBit(46015) == 1
        36000 -> varps.getVarBit(46016) == 1
        36001 -> varps.getVarBit(46017) == 1
        35898 -> varps.getVarBit(46018) == 1
        44875 -> varps.getVarBit(48028) == 1
        45047 -> varps.getVarBit(48686) == 1
        45045 -> varps.getVarBit(48685) == 1
        44912 -> varps.getVarBit(49448) == 1
        44946 -> varps.getVarBit(49449) == 1
        45400 -> varps.getVarBit(49723) == 1
        45605 -> varps.getVarBit(41541) == 1
        45117 -> varps.getVarBit(21556) == 1
        45797 -> varps.getVarBit(50328) == 1
        51672 -> varps.getVarBit(58068) == 1
        45567 -> varps.getVarBit(50329) == 1
        3694 -> varps.getVarBit(51063) == 1
        43673 -> varps.getVarBit(4332) > 0
        41807 -> varps.getVarBit(51434) == 1
        46211 -> varps.getVarBit(51435) == 1
        41808 -> varps.getVarBit(51436) == 1
        46279 -> varps.getVarBit(51431) == 1
        46272 -> varps.getVarBit(51432) == 1
        41810 -> varps.getVarBit(51437) == 1
        41811 -> varps.getVarBit(51438) == 1
        46377 -> varps.getVarBit(51704) == 1
        47202 -> varps.getVarBit(52819) == 1
        48368 -> varps.getVarBit(53229) == 1
        48375 -> varps.getVarBit(53239) == 1
        48376 -> varps.getVarBit(53240) == 1
        48288 -> varps.getVarBit(53244) == 1
        48289 -> varps.getVarBit(53242) == 1
        48290 -> varps.getVarBit(53243) == 1
        48338 -> varps.getVarBit(53245) == 1
        48344 -> varps.getVarBit(53246) == 1
        48345 -> varps.getVarBit(53247) == 1
        48346 -> varps.getVarBit(53248) == 1
        48283 -> varps.getVarBit(53249) == 1
        48284 -> varps.getVarBit(53250) == 1
        48285 -> varps.getVarBit(53251) == 1
        49074 -> varps.getVarBit(54672) == 1
        49071 -> varps.getVar(11534) >= 1
        49552 -> varps.getVarBit(55118) == 1
        50077 -> varps.getVarBit(55725) == 1
        50067 -> varps.getVarBit(55726) == 1
        50069 -> varps.getVarBit(55727) == 1
        50068 -> varps.getVarBit(55728) == 1
        50228 -> varps.getVarBit(55985) == 1
        50696 -> varps.getVarBit(56288) == 1
        else -> false
    }
}

fun getEffectTimeRemaining(effect: Effect): Long {
    val buffTimeClientCycles = getEffectExpiryCycle(effect.structId)
    if (buffTimeClientCycles == 0) return 0
    val remainingCycles = buffTimeClientCycles - Bootstrap.client.clientCycle
    if (remainingCycles <= 0) return 0
    val buffTimeMilliseconds = (remainingCycles + 1) * 20L
    return buffTimeMilliseconds
}

fun getEffectStacks(effect: Effect): Int {
    return when (effect.structId) {
        14718 -> varps.getVarBit(1898)
        24333 -> varps.getVar(4678)
        31982 -> varps.getVarBit(28640)
        14675 -> varps.getVarBit(28708)
        32253 -> varps.getVarBit(29113)
        32249 -> varps.getVarBit(29115)
        32248 -> varps.getVarBit(29114)
        33651 -> varps.getVarBit(32623)
        33655, 33656, 33657, 28853 -> varps.getVarBit(32637)
        34984 -> varps.getVarBit(34314)
        32631 -> scale(varps.getVarBit(29799), 255, 100)
        28501 -> varps.getVarBit(1895)
        24189 -> scale(varps.getVar(6500), maxOf(1, getTransfigureValue()), 100)
        35799 -> varps.getVarBit(28719)
        35800 -> varps.getVarBit(28720)
        35801 -> varps.getVarBit(28721)
        35802, 35803 -> varps.getVarBit(28722)
        35804 -> scale(varps.getVarBit(30984), 50000, 100)
        40797 -> varps.getVarBit(41298)
        35816 -> scale(varps.getVarBit(1897) * 20, 100, 100)
        35817 -> varps.getVarBit(34871)
        35826 -> scale(varps.getVar(6091), 500000, 100)
        1392 -> varps.getVarBit(35735) * 10
        1408 -> varps.getVarBit(35737)
        1489 -> varps.getVar(12679)
        23174 -> varps.getVarBit(36217)
        1624 -> (equipment[2]?.varDomain?.getVar(30214) ?: 0) + (equipment[17]?.varDomain?.getVar(20171) ?: 0)
        1626 -> varps.getVarBit(36378)
        37424 -> varps.getVarBit(36042) / 5
        37425 -> 5
        31984 -> varps.getVarBit(38922)
        39037 -> varps.getVar(2735)
        39038 -> scale(varps.getVarBit(521) - varps.getVar(183), maxOf(1, varps.getVarBit(521)), 50 / 10)
        39140 -> varps.getVarBit(38967)
        39440 -> varps.getVarBit(39880)
        39571 -> minOf(4, varps.getVar(7845))
        39572 -> -1 * maxOf(-4, varps.getVar(7845))
        40124 -> varps.getVarBit(40601)
        40525 -> varps.getVarBit(41009)
        40798 -> varps.getVarBit(41299)
        680 -> varps.getVarBit(42160) / 10
        6938 -> varps.getVarBit(43703)
        6939 -> varps.getVarBit(43704)
        6940 -> varps.getVarBit(43692)
        6941 -> varps.getVarBit(43693)
        6942 -> varps.getVarBit(43694)
        6943 -> varps.getVarBit(43705)
        6944 -> varps.getVarBit(43695)
        6945 -> varps.getVarBit(43696)
        6946 -> varps.getVarBit(43706)
        6947 -> varps.getVarBit(43697)
        6948 -> varps.getVarBit(43707)
        6949 -> varps.getVarBit(43708)
        6950 -> varps.getVarBit(43698)
        6951 -> varps.getVarBit(43709)
        6952 -> varps.getVarBit(43699)
        6953 -> varps.getVarBit(43700)
        6954 -> varps.getVarBit(43701)
        6955 -> varps.getVarBit(43702)
        37130 -> varps.getVar(4574)
        35990 -> varps.getVarBit(47360)
        35991 -> varps.getVar(9307)
        44938 -> varps.getVar(9588)
        44939 -> varps.getVar(9589)
        44879 -> varps.getVarBit(48179)
        45035 -> varps.getVar(9667)
        45036 -> varps.getVar(9668)
        44996 -> varps.getVarBit(49291)
        44040 -> varps.getVar(9911)
        44042 -> varps.getVarBit(49527)
        44066 -> varps.getVar(9912)
        44820 -> varps.getVar(9913)
        45584 -> varps.getVarBit(50198)
        45275 -> varps.getVarBit(21565)
        45635 -> varps.getVar(10224)
        45557 -> varps.getVar(10254)
        45558 -> varps.getVar(10255)
        45559 -> varps.getVar(10256)
        45560 -> varps.getVar(10259)
        45561 -> varps.getVar(10257)
        516 -> varps.getVarBit(30947)
        517 -> varps.getVarBit(30948)
        32755 -> varps.getVarBit(30949)
        33226 -> varps.getVarBit(34893)
        33227 -> varcs.getVar(4276)
        34499 -> varcs.getVar(4277)
        46026 -> varps.getVar(10326)
        46027 -> varps.getVarBit(50812)
        3694 -> varps.getVarBit(51103)
        19620 -> 4 * bitCount(varps.getVarBit(51205))
        43673 -> varps.getVar(10434) * 3
        43678 -> varps.getVar(10432)
        43682 -> varps.getVar(10433)
        41787 -> varps.getVarBit(51494)
        46210 -> varps.getVarBit(51508)
        41806 -> varps.getVarBit(51509)
        46198 -> varps.getVar(10540)
        46199 -> varps.getVar(10541)
        46201 -> varps.getVar(10543)
        46203 -> varps.getVar(10545)
        46193 -> varps.getVar(10534)
        46194 -> varps.getVar(10537)
        46195 -> varps.getVar(10538)
        46197 -> varps.getVar(10539)
        29050 -> varps.getVarBit(56961)
        47182 -> varcs.getVar(7112)
        34898 -> varps.getVarBit(52528)
        48333 -> varps.getVar(10986)
        48334 -> varps.getVar(11035)
        48335 -> varps.getVar(10997)
        32349 -> varps.getVar(11823)
        48340 -> getBoneShieldLevel(varps.getVar(11065))
        48338 -> varps.getVar(11044)
        47806 -> varps.getVar(10951)
        47807 -> varps.getVar(10952)
        48350 -> varps.getVar(11085) % 5
        47801 -> varps.getVar(10936) % 6
        48850 -> varps.getVarBit(54609)
        49071 -> varps.getVar(11534)
        49074 -> varps.getVar(11545)
        49132 -> varps.getVar(8422)
        49133 -> varps.getVar(8423)
        49555 -> varps.getVarBit(27010)
        49562 -> varps.getVar(11612)
        49912 -> varps.getVar(11750)
        49998 -> varps.getVar(11770)
        50083 -> varps.getVar(11776)
        49999 -> varps.getVar(11771)
        50063 -> varps.getVar(11774)
        50207, 51496 -> varps.getVar(11834)
        50212 -> varps.getVarBit(55992)
        19673 -> varps.getVarBit(56861) * 10

        1869 -> varps.getVarBit(60643)
        6850 -> equipment[2]?.varDomain?.getVar(30214) ?: 0
        14885 -> getCurrentLevel(Skill.ATTACK) - getRealLevel(Skill.ATTACK)
        14886 -> getRealLevel(Skill.ATTACK) - getCurrentLevel(Skill.ATTACK)
        14887 -> getCurrentLevel(Skill.STRENGTH) - getRealLevel(Skill.STRENGTH)
        14888 -> getRealLevel(Skill.STRENGTH) - getCurrentLevel(Skill.STRENGTH)
        14889 -> getCurrentLevel(Skill.DEFENSE) - getRealLevel(Skill.DEFENSE)
        14890 -> getRealLevel(Skill.DEFENSE) - getCurrentLevel(Skill.DEFENSE)
        14891 -> getCurrentLevel(Skill.RANGED) - getRealLevel(Skill.RANGED)
        14892 -> getRealLevel(Skill.RANGED) - getCurrentLevel(Skill.RANGED)
        14895 -> getCurrentLevel(Skill.MAGIC) - getRealLevel(Skill.MAGIC)
        14896 -> getRealLevel(Skill.MAGIC) - getCurrentLevel(Skill.MAGIC)
        23175 -> getRealLevel(Skill.CONSTITUTION) - getCurrentLevel(Skill.CONSTITUTION)
        39959 -> getRealLevel(Skill.WOODCUTTING) - getCurrentLevel(Skill.WOODCUTTING)
        40253 -> getRealLevel(Skill.MINING) - getCurrentLevel(Skill.MINING)
        40381 -> getRealLevel(Skill.FISHING) - getCurrentLevel(Skill.FISHING)
        40414 -> getRealLevel(Skill.HUNTER) - getCurrentLevel(Skill.HUNTER)
        48289 -> getCurrentLevel(Skill.NECROMANCY) - getRealLevel(Skill.NECROMANCY)
        48290 -> getRealLevel(Skill.NECROMANCY) - getCurrentLevel(Skill.NECROMANCY)
        51841 -> varps.getVar(12264)
        51848 -> varps.getVar(12268)
        51850 -> varps.getVar(12277)
        52064 -> varps.getVar(12291) % 4
        52333 -> varps.getVar(12437)
        52342 -> varps.getVar(2735)
        52791 -> varps.getVar(12655)
        53000 -> varps.getVarBit(60830)
        53001 -> varps.getVarBit(60831)

        else -> 0
    }
}

private fun scale(value: Int, max: Int, targetMax: Int) = (value * targetMax) / max

private fun getTransfigureValue(): Int {
    return 1 // Placeholder
}

private fun getBoneShieldLevel(structId: Int): Int { // script17460 — buff-bar stack getter (script11077 case 48340)
    val int1 = when (structId) {
        48326 -> 25
        48327 -> 50
        else -> 0
    }
    val int2 = if (equipment[13]?.getDef()?.params?.get(8928) == 49089 && varps.getVarBit(54731) == 2) 15 else 0
    return scale(getRealLevel(Skill.NECROMANCY), 100, int1) + int2
}