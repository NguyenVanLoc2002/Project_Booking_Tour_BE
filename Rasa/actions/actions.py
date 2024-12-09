from typing import Any, Text, Dict, List
from rasa_sdk import Action, Tracker
from rasa_sdk.executor import CollectingDispatcher
from rasa_sdk.events import UserUtteranceReverted
from rasa_sdk.events import SlotSet
import requests
from requests.exceptions import JSONDecodeError, RequestException
import logging

# Cấu hình logging
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)

class ActionShowNorthernTours(Action):
    def name(self) -> str:
        return "action_show_northern_tours"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        # URL của TourService qua API Gateway
        tour_service_url = "http://api_gateway:8000/api/v1/tours/region?region=NORTH&page=1&size=4&isAscending=false"

        try:
            response = requests.get(tour_service_url)
            response.raise_for_status()
            data = response.json()  # Lấy dữ liệu JSON từ phản hồi của API

            # Lấy danh sách các tour từ trường 'content'
            tours = data.get('content', [])

            # Tạo câu trả lời từ dữ liệu tour
            tour_list = [
                {
                    "title": f"{tour['name']}: {tour['day']} ngày, giá {tour['price']} VNĐ",
                    "buttons": [
                        {
                            "title": "Chi tiết Tour",  # Nút để nhấn vào chi tiết tour
                            "payload": f"{tour['ticketId']}"  # Liên kết khi click
                        }
                    ]
                }
                for tour in tours
            ]

            # Cung cấp thông tin các tour qua Rich Content (ví dụ: Cards)
            for tour in tour_list:
                dispatcher.utter_message(
                    text=tour["title"], 
                    buttons=[{"title": button["title"], "url": button["payload"]} for button in tour["buttons"]]
                )

        except requests.exceptions.RequestException as e:
            dispatcher.utter_message(text="Xin lỗi, hiện không thể lấy thông tin tour. Vui lòng thử lại sau.")
            print(f"Error: {e}")

        return []

class ActionShowCentralTours(Action):
    def name(self) -> str:
        return "action_show_central_tours"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        # URL của TourService qua API Gateway
        tour_service_url = "http://api_gateway:8000/api/v1/tours/region?region=CENTRAL&page=1&size=4&isAscending=false"

        try:
            response = requests.get(tour_service_url)
            response.raise_for_status()
            data = response.json()  # Lấy dữ liệu JSON từ phản hồi của API

            # Lấy danh sách các tour từ trường 'content'
            tours = data.get('content', [])

            # Tạo câu trả lời từ dữ liệu tour
            tour_list = [
                {
                    "title": f"{tour['name']}: {tour['day']} ngày, giá {tour['price']} VNĐ",
                    "buttons": [
                        {
                            "title": "Chi tiết Tour",  # Nút để nhấn vào chi tiết tour
                            "payload": f"{tour['ticketId']}"    # Liên kết khi click
                        }
                    ]
                }
                for tour in tours
            ]

            # Cung cấp thông tin các tour qua Rich Content (ví dụ: Cards)
            for tour in tour_list:
                dispatcher.utter_message(
                    text=tour["title"], 
                    buttons=[{"title": button["title"], "url": button["payload"]} for button in tour["buttons"]]
                )

        except requests.exceptions.RequestException as e:
            dispatcher.utter_message(text="Xin lỗi, hiện không thể lấy thông tin tour. Vui lòng thử lại sau.")
            print(f"Error: {e}")

        return []

class ActionShowSouthTours(Action):
    def name(self) -> str:
        return "action_show_south_tours"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        # URL của TourService qua API Gateway
        tour_service_url = "http://api_gateway:8000/api/v1/tours/region?region=NORTH&page=1&size=4&isAscending=false"

        try:
            response = requests.get(tour_service_url)
            response.raise_for_status()
            data = response.json()  # Lấy dữ liệu JSON từ phản hồi của API

            # Lấy danh sách các tour từ trường 'content'
            tours = data.get('content', [])

            # Tạo câu trả lời từ dữ liệu tour
            tour_list = [
                {
                    "title": f"{tour['name']}: {tour['day']} ngày, giá {tour['price']} VNĐ",
                    "buttons": [
                        {
                            "title": "Chi tiết Tour",  # Nút để nhấn vào chi tiết tour
                            "payload": f"{tour['ticketId']}"                          }
                    ]
                }
                for tour in tours
            ]

            # Cung cấp thông tin các tour qua Rich Content (ví dụ: Cards)
            for tour in tour_list:
                dispatcher.utter_message(
                    text=tour["title"], 
                    buttons=[{"title": button["title"], "url": button["payload"]} for button in tour["buttons"]]
                )

        except requests.exceptions.RequestException as e:
            dispatcher.utter_message(text="Xin lỗi, hiện không thể lấy thông tin tour. Vui lòng thử lại sau.")
            print(f"Error: {e}")

        return []


class ActionDefaultFallback(Action):
    def name(self) -> str:
        return "action_default_fallback"

    def run(self, dispatcher: CollectingDispatcher, tracker: Tracker, domain: dict) -> list:
        # Gửi thông điệp khi không nhận diện được intent
        dispatcher.utter_message(text="Vui lòng đặt một câu hỏi khác, câu hỏi hiện tại của bạn không phù hợp.")

         # Hoàn tác lời nói của người dùng, trả về để bot không ghi nhận
        return [UserUtteranceReverted()]
