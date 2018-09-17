


const PROXY_CONFIG = {
  "/api": {
    "target": "http://main.account.jiahou.com:8001",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "pathRewrite": {
      "^/api": "/"
    },
    "bypass": function (req, res, proxyOptions) {
      req.headers["host"] = "race";
      req.headers["referer"] = "https://raceqa.saic-gm.com/";
    }
  }
}

module.exports = PROXY_CONFIG;
