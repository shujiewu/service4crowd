FROM continuumio/anaconda3:latest
RUN pip install --upgrade pip
RUN pip install pika
RUN pip install commands
ADD runtime.py /home/runtime/runtime.py
ENTRYPOINT ["python","/home/runtime/runtime.py"]