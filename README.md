# Cryptomoon

## Configuration

### config.json

this file contains:
* Portfolio list
* Datasource configuration (supports only MYSQL)
* Tickers API configuration (CoinMarketCap & LiveCoinWatch are mandatory)
* Connectors API configuration
* Eventually UI settings

_config.json_
```json
{
  "portfolios": {
    "main": [
      "KRAKEN",
      "BINANCE"
    ],
    "second": [
      "KRAKEN",
      "ZELCORE"
    ]
  },
  "datasource": {
    "type": "MYSQL",
    "username": "cryptomoon",
    "password": "[[ REPLACE_BY_DATABASE_PASSWORD ]]",
    "database": "cryptomoon",
    "url": "jdbc:mysql://localhost:3306/cryptomoon"
  },
  "tickers": [
    {
      "type": "COINMARKETCAP",
      "apiKey": "[[ REPLACE_BY_COINMARKETCAP_API_KEY ]]"
    },
    {
      "type": "LIVECOINWATCH",
      "apiKey": "[[ REPLACE_BY_LIVECOINWATCH_API_KEY ]]"
    }
  ],
  "connectors": [
    {
      "type": "BINANCE",
      "apiKey": "[[ REPLACE_BY_BINANCE_API_KEY ]]",
      "secretKey": "[[ REPLACE_BY_BINANCE_SECRET_KEY ]]"
    },
    {
      "type": "KRAKEN",
      "apiKey": "[[ REPLACE_BY_KRAKEN_API_KEY ]]",
      "secretKey": "[[ REPLACE_BY_KRAKEN_SECRET_KEY ]]"
    }
  ]
}
```

### Wallets.json

this file contains:
* collection of accounts with currency balances

_wallets.json_
```json
{
  "BINANCE": {
    "SHIB": 1067688.37,
    "DOGE": 214.52793
  },
  "ZELCORE": {
    "RTM": 14563.24
  }
}
```

## Docker

```bash
docker run --net=host -v CONFIG_DIRECTORY:/config chsfleury/cryptomoon
```