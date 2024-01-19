import re

import yaml
from graphviz import Digraph


def parse_yaml(file_path):
    try:
        with open(file_path, 'r') as file:
            data = yaml.safe_load(file)
            return data
    except yaml.YAMLError as e:
        print(f"Error parsing YAML: {e}")
        return None


def extract_action_name(input_string):
    if input_string is None:
        return ''
    match = re.search(r'\.([^.]*)\(\)', input_string)
    return match.group(1) if match else ''


def create_node_label(node):
    action_name = extract_action_name(node['create-exp'])
    return f"{node['id']}({action_name})" if action_name else node['id']


def generate_flowchart_node(flowchart, node):
    label = create_node_label(node)
    shape = 'ellipse' if node['id'] in ('start', 'end') else 'box'
    flowchart.node(node['id'], label=label, shape=shape)


def generate_flowchart_edges(flowchart, node):
    if node.get('start'):
        flowchart.edge('start', node['id'], label='')
    if node.get('end'):
        flowchart.edge(node['id'], 'end')
    if node.get('conditions'):
        for condition in node['conditions']:
            when_nodes = condition.get('when-nodes', [])
            create_exp_list = [create_exp_condition(item) for item in when_nodes]
            when = ', '.join(create_exp_list)
            for to_node in condition['to-nodes']:
                flowchart.edge(node['id'], to_node, label=when)


def generate_flowchart():
    for flow in parsed_data.get('pay-flow', {}).get('flows', []):
        flowchart = Digraph(flow['id'], format='png')
        flowchart.node('start', label='开始', shape='ellipse', style='filled', fillcolor='lightblue', width='1')
        flowchart.node('end', label='结束', shape='ellipse', style='filled', fillcolor='lightgreen', width='1')

        for node in flow.get('nodes', []):
            generate_flowchart_node(flowchart, node)
            generate_flowchart_edges(flowchart, node)

        flowchart.attr(nodesep='0.8', ranksep='0.8')
        flowchart.render(filename=f'src/main/resources/image/{flow["id"]}', format='png', cleanup=True)


def create_exp_condition(item):
    create_exp = extract_action_name(item.get('create-exp')) or ''
    simple_exp = item.get('simple-exp') or ''
    return f"Not ({create_exp})" if item.get(
        'is-negated') else f"{create_exp}, {simple_exp}" if create_exp and simple_exp else create_exp or simple_exp


if __name__ == '__main__':
    file_path = 'src/main/resources/application.yml'
    parsed_data = parse_yaml(file_path)
    generate_flowchart()
