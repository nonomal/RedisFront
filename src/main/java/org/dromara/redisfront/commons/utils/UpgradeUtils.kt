package org.dromara.redisfront.commons.utils

import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONUtil
import org.dromara.redisfront.commons.constant.Constants

/**
 *  Upgrade
 *  @author Jin
 */
open class UpgradeUtils {
    companion object {
        private const val checkUrl = "https://gitee.com/dromara/RedisFront/raw/master/assets/version.json"
        private const val releaseUrl = "https://gitee.com/dromara/RedisFront/releases/"

        @JvmStatic
        fun checkVersion() {
            val currentVersion = Constants.APP_VERSION
            if (currentVersion.startsWith("@")) {
                return
            }

            val httpRequest = HttpUtil.createGet(checkUrl)
            val httpResponse = httpRequest.execute()

            if (httpResponse.isOk) {

                val body = httpResponse.body()
                val versionObject = JSONUtil.parseObj(body)
                val newVersion = versionObject.getStr("version")
                if (RedisFrontUtils.equal(currentVersion, newVersion)) {
                    return
                }

                val currentVersionArray = currentVersion.split(".")
                val newVersionArray = newVersion.split(".")

                if (currentVersion.length == newVersion.length) {
                    val xIsTrue = RedisFrontUtils.equal(currentVersionArray[0], newVersionArray[0])
                    val yIsTrue = RedisFrontUtils.equal(currentVersionArray[1], newVersionArray[1])
                    val zIsTrue = RedisFrontUtils.equal(currentVersionArray[2], newVersionArray[2])
                    if (xIsTrue && yIsTrue && zIsTrue) {
                        return
                    }
                }


            }
        }
    }
}
