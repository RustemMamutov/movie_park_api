# -*- coding: utf-8 -*-
from jinja2 import Template
import json

template_string = open('application.properties.j2', 'r').read()
variables_dict = json.loads(open('variables.json', 'r').read())

template = Template(template_string)

result = template.render(variables_dict)

file = open('application.properties', 'w')
file.write(result)
print(result)
file.close()
