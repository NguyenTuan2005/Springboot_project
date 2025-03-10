import time
from pytrends.request import TrendReq
from pytrends.exceptions import TooManyRequestsError
import sys
import json

def fetch_trends(keyword):
    """
    Fetches Google Trends data for the specified keyword over the past month,
    and prints a JSON string containing the keyword, trend score, and date.
    If the request is rate-limited or no data is available, prints an error message.
    """
    # Initialize the pytrends client with language and timezone settings
    pytrends = TrendReq(hl='en-US', tz=360)
    # Build the payload for the given keyword and timeframe (today 1-m indicates past 1 month)
    pytrends.build_payload([keyword], timeframe='today 1-m')

    try:
        interest_over_time_df = pytrends.interest_over_time()
    except TooManyRequestsError:
        print(json.dumps({"error": "Too many requests. Please try again later."}))
        sys.exit(1)
    except Exception as e:
        print(json.dumps({"error": f"Unexpected error: {str(e)}"}))
        sys.exit(1)

    if not interest_over_time_df.empty:
        interest_over_time_df = interest_over_time_df.infer_objects()
        interest_over_time_df = interest_over_time_df.fillna(False)
        try:
            trend_score = int(interest_over_time_df[keyword].iloc[-1])
        except Exception as e:
            print(json.dumps({"error": f"Error processing trend score: {str(e)}"}))
            sys.exit(1)

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
