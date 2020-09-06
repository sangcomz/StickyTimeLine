package xyz.sangcomz.stickytimeline

import java.util.*

/**
 * Created by paetztm on 2/6/2017.
 */

class SingerRepo {
    //solo
    val singerList: List<Singer>
        get() {
            val singerList = ArrayList<Singer>()
            singerList.add(Singer("Solo", "1995.04", "Lim ChangJung"))

            singerList.add(Singer("FIN.K.L", "1998.05", "Lee Jin"))
            singerList.add(Singer("FIN.K.L", "1998.05", "Sung YuRi"))
            singerList.add(Singer("FIN.K.L", "1998.05", "Oak JooHyun"))
            singerList.add(Singer("FIN.K.L", "1998.05", "Lee HyoRi"))

            singerList.add(Singer("Solo", "1999.04", "Kim BumSoo"))

            singerList.add(Singer("Solo", "1999.11", "Park HyoShin"))
            singerList.add(Singer("Solo", "1999.11", "Lee SooYoung"))
            singerList.add(Singer("Solo", "2000.11", "Sung SiKyung"))

            singerList.add(Singer("Buzz", "2003.10", "Kim Yeah"))
            singerList.add(Singer("Buzz", "2003.10", "Yun WooHyun"))
            singerList.add(Singer("Buzz", "2003.10", "Sin JunKi"))
            singerList.add(Singer("Buzz", "2003.10", "Min KyungHoon"))

            singerList.add(Singer("Solo", "2006.06", "Yunha"))

            singerList.add(Singer("Girls' Generation", "2007.08", "TaeYeon"))
            singerList.add(Singer("Girls' Generation", "2007.08", "Sunny"))
            singerList.add(Singer("Girls' Generation", "2007.08", "Tiffany"))
            singerList.add(Singer("Girls' Generation", "2007.08", "HyoYeon"))
            singerList.add(Singer("Girls' Generation", "2007.08", "YuRi"))
            singerList.add(Singer("Girls' Generation", "2007.08", "SooYoung"))
            singerList.add(Singer("Girls' Generation", "2007.08", "YoonA"))
            singerList.add(Singer("Girls' Generation", "2007.08", "SeoHyun"))

            singerList.add(Singer("Wanna One", "2017.08", "Kang Daniel"))
            singerList.add(Singer("Wanna One", "2017.08", "Lai Kuan Lin"))
            singerList.add(Singer("Wanna One", "2017.08", "Ong SeongWu"))
            singerList.add(Singer("Wanna One", "2017.08", "Ha SungWoon"))
            singerList.add(Singer("Wanna One", "2017.08", "Yoon JiSung"))
            singerList.add(Singer("Wanna One", "2017.08", "Park WooJin"))
            singerList.add(Singer("Wanna One", "2017.08", "Lee DaeHwi"))
            singerList.add(Singer("Wanna One", "2017.08", "Kim JaeHwan"))
            singerList.add(Singer("Wanna One", "2017.08", "Bae JinYoung"))
            singerList.add(Singer("Wanna One", "2017.08", "Hwang MinHyun"))
            singerList.add(Singer("Wanna One", "2017.08", "Park JiHoon"))

            singerList.add(Singer("Solo", "2017.11", "Woo WonJae"))

            return singerList
        }
}
