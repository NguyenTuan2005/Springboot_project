import sys
import json
from pytrends.request import TrendReq

def fetch_trends(keyword):
    pytrends = TrendReq(hl='en-US', tz=360)
    pytrends.build_payload([keyword], timeframe='today 1-m')

    interest_over_time_df = pytrends.interest_over_time()

    if not interest_over_time_df.empty:
        # Get the most recent trend scor
        trend_score = int(interest_over_time_df[keyword].iloc[-1])
        result = {
            "keyword": keyword,
            "trendScore": trend_score,
            "date": interest_over_time_df.index[-1].strftime('%Y-%m-%d')
        }
        print(json.dumps(result))
    else:
        print(json.dumps({"error": "No data available"}))

if __name__ == "__main__":
    if len(sys.argv) > 1:
        fetch_trends(sys.argv[1])
    else:
        print(json.dumps({"error": "No keyword provided"}))

