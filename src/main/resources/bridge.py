import importlib
import importlib.util
import sys
import os

from org.slf4j import LoggerFactory as lf
from typing import Callable

logger = lf.getLogger('Python')
cwd = None

def setup():
    global logger
    sys.path.append('.')
    cwd = os.getcwd()
    logger.debug('sys.path = ' + str(sys.path))
    logger.debug('os.getcwd() = ' + str(cwd))

def import_file(module_name, path):
    spec = importlib.util.spec_from_file_location(module_name, path)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    sys.modules[module_name] = module
    return module

def invoke_entrypoint(module, name, kwargs):
    try:
        on_load = getattr(module, name)
        on_load(**kwargs)
    except AttributeError as e:
        logger.error(e)
        pass

logger.info("Initialized Python Bridge.")