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
            singerList.add(Singer("Solo", "1995.04", "Lim Chang Jung"))

            singerList.add(Singer("FIN.K.L", "1998.05", "Lee Jin"))
            singerList.add(Singer("FIN.K.L", "1998.05", "Sung Yu Ri"))
            singerList.add(Singer("FIN.K.L", "1998.05", "Oak Joo Hyun"))
            singerList.add(Singer("FIN.K.L", "1998.05", "Lee Hyo Ri"))

            singerList.add(Singer("Solo", "1999.04", "Kim Bumsoo"))
            singerList.add(Singer("Solo", "1999.11", "Park Hyo Shin"))
            singerList.add(Singer("Solo", "1999.11", "Lee Soo Young"))
            singerList.add(Singer("Solo", "2000.11", "Sung Si Kyung"))

            singerList.add(Singer("Buzz", "2003.10", "Kim Yeah"))
            singerList.add(Singer("Buzz", "2003.10", "Yun Woo Hyun"))
            singerList.add(Singer("Buzz", "2003.10", "Sin Jun Ki"))
            singerList.add(Singer("Buzz", "2003.10", "Min Kyung Hoon"))

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
            singerList.add(Singer("Wanna One", "2017.08", "Ong Seong Wu"))
            singerList.add(Singer("Wanna One", "2017.08", "Ha Sung Woon"))
            singerList.add(Singer("Wanna One", "2017.08", "Yoon Ji Sung"))
            singerList.add(Singer("Wanna One", "2017.08", "Park Woo Jin"))
            singerList.add(Singer("Wanna One", "2017.08", "Lee Dae Hwi"))
            singerList.add(Singer("Wanna One", "2017.08", "Kim Jae Hwan"))
            singerList.add(Singer("Wanna One", "2017.08", "Bae Jin Young"))
            singerList.add(Singer("Wanna One", "2017.08", "Hwang Min Hyun"))
            singerList.add(Singer("Wanna One", "2017.08", "Park Ji Hoon"))

            singerList.add(Singer("Solo", "2017.11", "Woo Won Jae"))

            return singerList
        }
}
