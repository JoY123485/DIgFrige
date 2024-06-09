import csv

def process_csv(input_file, output_file):
    # 그룹화할 딕셔너리 초기화
    grouped_data = {}

    # CSV 파일 읽기
    with open(input_file, 'r', newline='', encoding='utf-8-sig') as csvfile:
        reader = csv.reader(csvfile)
        next(reader)  # 헤더 건너뛰기

        for row in reader:
            recipe_id = row[0]
            title = row[1]
            url = row[2]
            ingredients = row[3][:-2].split(', ')

            # 키가 이미 존재하면 재료를 추가하고, 아니면 새로운 그룹 생성
            if recipe_id in grouped_data:
                grouped_data[recipe_id][2].append(ingredients)  # 재료 추가
            else:
                grouped_data[recipe_id] = [title, url, [ingredients]]

    # 새로운 CSV 파일에 쓰기
    with open(output_file, 'w', newline='', encoding='utf-8-sig') as output_csvfile:
        writer = csv.writer(output_csvfile)
        writer.writerow(['title', 'url', 'ingredient'])  # 헤더 쓰기

        for group in grouped_data.values():
            writer.writerow([group[0], group[1], group[2]])

# CSV 파일 처리
process_csv('data/recipes.csv', 'data/grouped_recipes.csv')
