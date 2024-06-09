import requests
from bs4 import BeautifulSoup
import csv
import os
import urllib3

# SSL 경고 비활성화
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

baseUrl = 'http://www.10000recipe.com/recipe/'

def CrawlingBetweenRanges(startRecipeId, endRecipeId):
    os.makedirs('data', exist_ok=True)
    file_path = os.path.join('data', 'recipes.csv')
    with open(file_path, 'w', newline='', encoding='utf-8-sig') as csvfile:
        fieldnames = ['recipe_id', 'title', 'url', 'ingredient']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()
        
        crawled_recipes = set()  # 중복 방지
        
        for i in range(startRecipeId, endRecipeId):
            if i in crawled_recipes:
                continue  # 이미 크롤링된 레시피 건너뛰기
            
            if i % 10 == 0:
                print("count: " + str(i))
            
            res = PageCrawler(str(i))
            if res is None:
                continue
            
            title, url, ingredients = res
            for ingredient in ingredients:
                writer.writerow({
                    'recipe_id': i,
                    'title': title,
                    'url': url,
                    'ingredient': ingredient
                })
            
            crawled_recipes.add(i)  # 크롤링된 레시피 ID 추가

def PageCrawler(recipeId):
    url = baseUrl + recipeId

    try:
        page = requests.get(url, verify=False)
        soup = BeautifulSoup(page.content, 'html.parser')
    except requests.exceptions.RequestException as e:
        print(f"Error fetching {url}: {e}")
        return None

    try:
        res = soup.find('div', class_='view2_summary')
        recipe_title = res.find('h3').get_text().strip()
        recipe_url = url
        ingredients_section = soup.find('div', class_='ready_ingre3')
    except AttributeError:
        return None

    ingredients = []

    try:
        for ul in ingredients_section.find_all('ul'):
            for li in ul.find_all('li'):
                ingredient = li.get_text().strip().replace('\n', '').replace(' ', '')
                ingredients.append(ingredient)
    except AttributeError:
        return None

    return recipe_title, recipe_url, ingredients

# 크롤링 시작
CrawlingBetweenRanges(startRecipeId=7000000, endRecipeId=7400000)
