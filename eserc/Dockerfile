# Usa un'immagine di Python ufficiale come base
FROM python:3.9-slim

# Imposta la directory di lavoro nell'immagine
WORKDIR /app

# Copia il codice dell'applicazione nell'immagine
COPY . .

# Installa le dipendenze dell'applicazione
RUN pip install flask

# Esponi la porta 5000 per consentire il traffico HTTP
EXPOSE 5000

# Avvia l'applicazione Flask quando il contenitore viene avviato
CMD ["python", "servertap.py"]
