package test

import android.annotation.SuppressLint
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonArray
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 *
 *
 * test
 */
val calc = SarCalculator()
fun main() {

    Schedulers.io().scheduleDirect {
        binanceMain()
    }

    Schedulers.io().scheduleDirect {
        gateIoMain()
    }

    while (true) {
        Thread.sleep(Long.MAX_VALUE - 1)
    }

}

fun binanceMain() {
    val SYMBOLS =
        "[\"BTCUSDT\",\"ETHUSDT\",\"BNBUSDT\",\"NEOUSDT\",\"LTCUSDT\",\"QTUMUSDT\",\"ADAUSDT\",\"XRPUSDT\",\"EOSUSDT\",\"TUSDUSDT\",\"IOTAUSDT\",\"XLMUSDT\",\"ONTUSDT\",\"TRXUSDT\",\"ETCUSDT\",\"ICXUSDT\",\"NULSUSDT\",\"VETUSDT\",\"USDCUSDT\",\"LINKUSDT\",\"WAVESUSDT\",\"HOTUSDT\",\"ZILUSDT\",\"ZRXUSDT\",\"FETUSDT\",\"BATUSDT\",\"XMRUSDT\",\"ZECUSDT\",\"IOSTUSDT\",\"CELRUSDT\",\"DASHUSDT\",\"THETAUSDT\",\"ENJUSDT\",\"MATICUSDT\",\"ATOMUSDT\",\"TFUELUSDT\",\"ONEUSDT\",\"FTMUSDT\",\"ALGOUSDT\",\"DOGEUSDT\",\"DUSKUSDT\",\"ANKRUSDT\",\"COSUSDT\",\"COCOSUSDT\",\"MTLUSDT\",\"TOMOUSDT\",\"DENTUSDT\",\"KEYUSDT\",\"DOCKUSDT\",\"CHZUSDT\",\"BANDUSDT\",\"BUSDUSDT\",\"XTZUSDT\",\"RVNUSDT\",\"HBARUSDT\",\"NKNUSDT\",\"STXUSDT\",\"KAVAUSDT\",\"ARPAUSDT\",\"IOTXUSDT\",\"RLCUSDT\",\"CTXCUSDT\",\"BCHUSDT\",\"TROYUSDT\",\"OGNUSDT\",\"BTSUSDT\",\"BNTUSDT\",\"LTOUSDT\",\"MBLUSDT\",\"COTIUSDT\",\"STPTUSDT\",\"SOLUSDT\",\"CTSIUSDT\",\"HIVEUSDT\",\"CHRUSDT\",\"MDTUSDT\",\"STMXUSDT\",\"KNCUSDT\",\"LRCUSDT\",\"COMPUSDT\",\"SCUSDT\",\"ZENUSDT\",\"SNXUSDT\",\"DGBUSDT\",\"SXPUSDT\",\"MKRUSDT\",\"STORJUSDT\",\"MANAUSDT\",\"YFIUSDT\",\"BALUSDT\",\"BLZUSDT\",\"ANTUSDT\",\"CRVUSDT\",\"SANDUSDT\",\"OCEANUSDT\",\"NMRUSDT\",\"DOTUSDT\",\"LUNAUSDT\",\"RSRUSDT\",\"PAXGUSDT\",\"TRBUSDT\",\"SUSHIUSDT\",\"KSMUSDT\",\"EGLDUSDT\",\"DIAUSDT\",\"RUNEUSDT\",\"UMAUSDT\",\"BELUSDT\",\"WINGUSDT\",\"UNIUSDT\",\"OXTUSDT\",\"AVAXUSDT\",\"ORNUSDT\",\"UTKUSDT\",\"XVSUSDT\",\"ALPHAUSDT\",\"AAVEUSDT\",\"NEARUSDT\",\"FILUSDT\",\"INJUSDT\",\"AUDIOUSDT\",\"CTKUSDT\",\"AKROUSDT\",\"AXSUSDT\",\"HARDUSDT\",\"UNFIUSDT\",\"ROSEUSDT\",\"AVAUSDT\",\"SKLUSDT\",\"GRTUSDT\",\"1INCHUSDT\",\"REEFUSDT\",\"CELOUSDT\",\"RIFUSDT\",\"TRUUSDT\",\"CKBUSDT\",\"TWTUSDT\",\"LITUSDT\",\"SFPUSDT\",\"DODOUSDT\",\"CAKEUSDT\",\"BADGERUSDT\",\"OMUSDT\",\"PONDUSDT\",\"DEGOUSDT\",\"ALICEUSDT\",\"LINAUSDT\",\"PERPUSDT\",\"SUPERUSDT\",\"CFXUSDT\",\"PUNDIXUSDT\",\"TLMUSDT\",\"BAKEUSDT\",\"BURGERUSDT\",\"SLPUSDT\",\"SHIBUSDT\",\"ICPUSDT\",\"ARUSDT\",\"MDXUSDT\",\"MASKUSDT\",\"LPTUSDT\",\"GTCUSDT\",\"ERNUSDT\",\"KLAYUSDT\",\"PHAUSDT\",\"BONDUSDT\",\"C98USDT\",\"CLVUSDT\",\"QNTUSDT\",\"FLOWUSDT\",\"TVKUSDT\",\"MINAUSDT\",\"QUICKUSDT\",\"REQUSDT\",\"WAXPUSDT\",\"DYDXUSDT\",\"IDEXUSDT\",\"GALAUSDT\",\"YGGUSDT\",\"DFUSDT\",\"FIDAUSDT\",\"RADUSDT\",\"BETAUSDT\",\"LAZIOUSDT\",\"AUCTIONUSDT\",\"DARUSDT\",\"BNXUSDT\",\"ENSUSDT\",\"KP3RUSDT\",\"QIUSDT\",\"PORTOUSDT\",\"JASMYUSDT\",\"PYRUSDT\",\"RNDRUSDT\",\"ALCXUSDT\",\"SANTOSUSDT\",\"MCUSDT\",\"BICOUSDT\",\"FLUXUSDT\",\"FXSUSDT\",\"HIGHUSDT\",\"PEOPLEUSDT\",\"SPELLUSDT\",\"JOEUSDT\",\"ACHUSDT\",\"IMXUSDT\",\"GLMRUSDT\",\"API3USDT\",\"WOOUSDT\",\"ASTRUSDT\",\"GMTUSDT\",\"KDAUSDT\",\"APEUSDT\",\"BSWUSDT\",\"REIUSDT\",\"GALUSDT\",\"LDOUSDT\",\"EPXUSDT\",\"OPUSDT\",\"LEVERUSDT\",\"STGUSDT\",\"LUNCUSDT\",\"GMXUSDT\",\"POLYXUSDT\",\"APTUSDT\",\"OSMOUSDT\",\"HFTUSDT\",\"PHBUSDT\",\"HOOKUSDT\",\"MAGICUSDT\",\"AGIXUSDT\",\"GNSUSDT\",\"SYNUSDT\",\"VIBUSDT\",\"SSVUSDT\",\"LQTYUSDT\",\"AMBUSDT\",\"USTCUSDT\",\"IDUSDT\",\"ARBUSDT\",\"OAXUSDT\",\"RDNTUSDT\",\"EDUUSDT\",\"SUIUSDT\",\"PEPEUSDT\",\"FLOKIUSDT\"]"

    val SYMBOLS_JsonArr = Gson().fromJson(SYMBOLS, JsonArray::class.java)

    Schedulers.io().schedulePeriodicallyDirect({
        SYMBOLS_JsonArr.asList().forEach {
            getSymbolData_2H(it.asString)
        }
    }, 0, 2 * 60, TimeUnit.MINUTES)


}


class SarCalculator {
    private var accelerationFactor = 0.001f // 加速因子
    private var maxAccelerationFactor = 0.01f // 最大加速因子
    fun calculate(
        high: FloatArray,
        low: FloatArray,
        time: ArrayList<String>,
        symbol: String,
        acf: Float,
        rate: String,
        amount: ArrayList<Float>
    ): FloatArray? {
        if (high.size == 0 || low.size == 0) return null;
        accelerationFactor = acf;
        maxAccelerationFactor = accelerationFactor * 2
        val length = high.size
        val sar = FloatArray(length)
        val af = FloatArray(length)
        var uptrend = true // 上涨趋势
        var downtrendNum = 0;
        var changeTime = 0;
        var ep = low[0] // 极值
        val sar0 = high[0] // SAR初始值
        val af0 = accelerationFactor
        sar[0] = sar0
        af[0] = af0
        var downTimes = 0;
        var isSend = false;

        var upTimes = 0;

        for (i in 1 until length) {
            if (uptrend) {
                ep = Math.max(ep, high[i])
                sar[i] = sar[i - 1] + af[i - 1] * (ep - sar[i - 1])
            } else {
                ep = Math.min(ep, low[i])
                sar[i] = sar[i - 1] + af[i - 1] * (ep - sar[i - 1])
            }
            if (uptrend && low[i] < sar[i]) { // 趋势转换为下跌
                downTimes++
                uptrend = false
                changeTime++
                downtrendNum = 1
                if (i == length - 1) {
//                    sendMessageToWeChat("下跌：" + symbol + ",时间：" + time.get(i))
                }
//                println("下跌：" + symbol + ",时间：" + time.get(i))
                sar[i] = ep // SAR重置为极值
                ep = high[i] // 极值更新为新高
                af[i] = accelerationFactor // 加速因子重置

            } else if (!uptrend && high[i] > sar[i]) { // 趋势转换为上涨
                upTimes++
                if (i == length - 1) {
                    isSend = true
                    sendMessageToWeChat(
                        "上涨：" + symbol + "，变化几次：" + changeTime + ",下跌持续时间：" + downtrendNum + ",时间：" + time.get(
                            i
                        ) + ",交易量倍数：" + rate + ",当前交易量是否最近最高点：" + (amount.max() == amount.get(
                            i
                        ))
                    )
                }
                uptrend = true
                sar[i] = ep // SAR重置为极值
                ep = low[i] // 极值更新为新低
                af[i] = accelerationFactor // 加速因子重置
            } else { // 保持当前趋势
                if (!uptrend) {
                    downtrendNum++;
                    downTimes++
                } else {
                    upTimes++
                }
                af[i] = Math.min(af[i - 1] + accelerationFactor, maxAccelerationFactor)
            }
        }

        /*        if (Math.abs(downTimes - upTimes) < 10 && isSend) {
                    sendMessageToWeChat(symbol + "->" + "上涨时间：" + upTimes + "->" + "下跌时间：" + downTimes + "->" + "变化次数：" + changeTime + "下跌持续时间：" + downtrendNum)
                }*/

        return sar
    }
}

fun getDate(timestamp: Long): String {

// 将时间戳转换为Date对象
    val date = Date(timestamp);

// 定义日期格式
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

// 格式化日期
    return sdf.format(date)
}

fun getSYMBOLS() {
    try {
        var symbols = Network.getSymbols();
        var asJsonArray = symbols.get("symbols").asJsonArray
        val arr = JsonArray();
        asJsonArray.forEach {
            if (it.asJsonObject.get("isMarginTradingAllowed").asBoolean) {
                var symbol = it.asJsonObject.get("symbol").asString;
                if (symbol.contains("USDT")) {
                    arr.add(symbol)
                }
            }
        }
        println(arr)
    } catch (e: Exception) {

    }


}

fun sendMessageToWeChat(message: String) {

    val baseMsg =
        "{\"msgtype\": \"text\",\"text\": {\"content\": \"替换\",\"mentioned_list\":[\"wangqing\"],\"mentioned_mobile_list\":[\"\"]}}";

    val replace = baseMsg.replace("替换", message)
    synchronized(Network::sendMsg) {
        try {
            Network.sendMsg(replace)
        } catch (e: Exception) {

        }
    }


}

var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
fun getSymbolData_2H(symbol: String) {

    try {
        val high = ArrayList<Float>()
        val low = ArrayList<Float>()
        val time = ArrayList<String>()
        val amount = ArrayList<Float>()
        val d = ArrayList<Float>();

        val json = Network.getSymBolKLine_2h(symbol)

        var asList = json.asList()

        for (i in 0 until asList.size - 1) {
            val it = asList.get(i)
            val t = sdf.format(Date(it.asJsonArray.get(0).asLong));
            time.add(t)
            var h = it.asJsonArray.get(2).asFloat;
            var l = it.asJsonArray.get(3).asFloat;
            high.add(h)
            low.add(l)
            d.add(h - l);
            amount.add(it.asJsonArray.get(5).asFloat)
        }


//        val acf = d.max()

        val acf = 0.02f
        var rate = 0f;
        if (amount.size > 2) {
            if (amount.get(amount.size - 2) == 0f) {
                rate = 2f;
            } else {
                rate = amount.get(amount.size - 1) / amount.get(amount.size - 2)
            }
        }

        if (rate > 2f && high.get(high.size - 1) / low.get(high.size - 1) < 1.1) {
            calc.calculate(
                high.toFloatArray(),
                low.toFloatArray(),
                time,
                "币安->" + symbol,
                acf,
                String.format("%.2f", rate),
                amount
            )
        }

    } catch (e: Exception) {

    }

}

fun gateIoMain() {
    try {
        val SYMBOLS = getGateIoSYMBOLS()

        Schedulers.io().schedulePeriodicallyDirect({
            SYMBOLS.forEach {
                try {
                    getSymbolData_2H_GATE_IO(it)
                } catch (e: Exception) {
                    println()
                }
            }
        }, 0, 60, TimeUnit.MINUTES)

    } catch (e: java.lang.Exception) {
        println()
    }

}

fun getSymbolData_2H_GATE_IO(symbol: String) {

    val high = ArrayList<Float>()
    val low = ArrayList<Float>()
    val amount = ArrayList<Float>()
    val time = ArrayList<String>()
    val d = ArrayList<Float>();

    val json = Network.getSymBolKLine_2h_GateIo(symbol)

    var asList = json.asList()

    for (i in 0 until asList.size - 1) {
        val it = asList.get(i)
        if (it.asJsonArray.size() > 0) {
            val t = sdf.format(Date(it.asJsonArray.get(0).asLong * 1000))
            time.add(t)
            var h = it.asJsonArray.get(3).asFloat;
            var l = it.asJsonArray.get(4).asFloat;
            high.add(h)
            low.add(l)
            amount.add(it.asJsonArray.get(1).asFloat)
            d.add(h - l);
        } else {
            println()
        }
    }

//        val acf = d.max()

    val acf = 0.02f
    var rate = 0f;
    if (amount.size > 2) {
        if (amount.get(amount.size - 2) == 0f) {
            rate = 2f;
        } else {
            rate = amount.get(amount.size - 1) / amount.get(amount.size - 2)
        }
    }
    if (rate >= 2f && high.get(high.size - 1) / low.get(high.size - 1) < 1.1) {
        calc.calculate(
            high.toFloatArray(),
            low.toFloatArray(),
            time,
            "GateIo->" + symbol,
            acf,
            String.format("%.2f", rate),
            amount
        )
    }

}

fun getGateIoSYMBOLS(): ArrayList<String> {
    val arr = ArrayList<String>()
    try {
        var symbols = Network.getGateIoSymbols();
        symbols.forEach {
            if (!it.asJsonObject.get("trade_status").asString.contains("un")) {
                var symbol =
                    it.asJsonObject.get("id").asString
                if (symbol.contains("USDT") && !symbol.contains("3S") && !symbol.contains("3L")) {
                    arr.add(symbol)
                }
            }
        }

    } catch (e: Exception) {
        println()
    }

    return arr;


}