import re

from graphviz import Digraph
import yaml


def parse_yaml(file_path):
    with open(file_path, 'r') as file:
        try:
            data = yaml.safe_load(file)
            return data
        except yaml.YAMLError as e:
            print(f"Error parsing YAML: {e}")
            return None


def extract_action_name(input_string):
    # 使用正则表达式匹配目标字符串
    match = re.search(r'\.([^.]*)\(\)', input_string)
    # 如果匹配成功，返回提取的字符串，否则返回 None
    return match.group(1) if match else None

def generate_flowchart():
    for flow in parsed_data['payflow']['flows']:
        flowchart = Digraph(flow['name'], format='png')
        flowchart.node('start', label='开始', shape='ellipse', style='filled', fillcolor='lightblue', width='1')
        flowchart.node('end', label='结束', shape='ellipse', style='filled', fillcolor='lightgreen', width='1')
        for node in flow['nodes']:
            flowchart.node(node['id'], label=node['id']+"("+extract_action_name(node['create-exp'])+")", shape='box')
            if node.get('start'):
                flowchart.edge('start',node['id'], label='')
            if node.get('end'):
                flowchart.edge(node['id'], 'end')
            if node.get('conditions'):
                for condition in node['conditions']:
                    if (condition is None):
                        when = ''
                    else:
                        create_exp_list = ["Not ("+extract_action_name(item['create-exp'])+")" if item.get('is-negated') else extract_action_name(item['create-exp']) for item in condition.get('node-whens', [])]
                        when = ', '.join(create_exp_list)
                    for to_node in condition['to-nodes']:
                        flowchart.edge(node['id'], to_node, label=when)

        flowchart.attr(nodesep='0.8', ranksep='0.8')
        flowchart.render(filename='src/main/resources/image/'+flow['name'], format='png', cleanup=True)

if __name__ == '__main__':
    file_path = 'src/main/resources/application.yml'
    parsed_data = parse_yaml(file_path)
    generate_flowchart()





