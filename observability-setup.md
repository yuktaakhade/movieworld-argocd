# Observability Setup Guide for MovieWorld Service

This guide provides detailed instructions for setting up Prometheus, Grafana, and Istio to monitor the MovieWorld service.

> **Note:** For complete configuration files and detailed setup instructions, please refer to the central observability setup folder at `E:\Finastra-poc\observability-setup\`.

## Prerequisites

- Kubernetes cluster (for Istio setup)
- Docker and Docker Compose (for local development)
- MovieWorld service running with observability configurations

## 1. Prometheus Setup

### 1.1 Local Development Setup with Docker Compose

Create a `prometheus.yml` configuration file:

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'movieworld'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['host.docker.internal:9090']
        labels:
          application: 'movieworld'
```

Create a Docker Compose file for Prometheus:

```yaml
version: '3'
services:
  prometheus:
    image: prom/prometheus:v2.45.0
    container_name: prometheus
    ports:
      - "9091:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/usr/share/prometheus/console_libraries'
      - '--web.console.templates=/usr/share/prometheus/consoles'
    restart: always
```

Start Prometheus:

```bash
docker-compose up -d
```

### 1.2 Kubernetes Setup

Create a ConfigMap for Prometheus configuration:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: prometheus-config
  namespace: monitoring
data:
  prometheus.yml: |
    global:
      scrape_interval: 15s
      evaluation_interval: 15s
    
    scrape_configs:
      - job_name: 'movieworld'
        kubernetes_sd_configs:
          - role: pod
        relabel_configs:
          - source_labels: [__meta_kubernetes_pod_label_app]
            regex: movieworld
            action: keep
          - source_labels: [__meta_kubernetes_pod_container_port_name]
            regex: http
            action: keep
        metrics_path: '/actuator/prometheus'
```

Deploy Prometheus:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: prometheus
  namespace: monitoring
spec:
  replicas: 1
  selector:
    matchLabels:
      app: prometheus
  template:
    metadata:
      labels:
        app: prometheus
    spec:
      containers:
      - name: prometheus
        image: prom/prometheus:v2.45.0
        args:
        - "--config.file=/etc/prometheus/prometheus.yml"
        - "--storage.tsdb.path=/prometheus"
        ports:
        - containerPort: 9090
        volumeMounts:
        - name: config-volume
          mountPath: /etc/prometheus
      volumes:
      - name: config-volume
        configMap:
          name: prometheus-config
---
apiVersion: v1
kind: Service
metadata:
  name: prometheus
  namespace: monitoring
spec:
  selector:
    app: prometheus
  ports:
  - port: 9090
    targetPort: 9090
  type: ClusterIP
```

Apply the configuration:

```bash
kubectl apply -f prometheus-config.yaml
kubectl apply -f prometheus-deployment.yaml
```

## 2. Grafana Setup

### 2.1 Local Development Setup with Docker Compose

Add Grafana to your Docker Compose file:

```yaml
version: '3'
services:
  prometheus:
    # ... (as defined above)
  
  grafana:
    image: grafana/grafana:10.0.3
    container_name: grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_USER=admin
      - GF_SECURITY_ADMIN_PASSWORD=admin
      - GF_USERS_ALLOW_SIGN_UP=false
    volumes:
      - grafana-storage:/var/lib/grafana
    restart: always
    depends_on:
      - prometheus

volumes:
  grafana-storage:
```

Start Grafana:

```bash
docker-compose up -d
```

### 2.2 Kubernetes Setup

Deploy Grafana:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: grafana
  namespace: monitoring
spec:
  replicas: 1
  selector:
    matchLabels:
      app: grafana
  template:
    metadata:
      labels:
        app: grafana
    spec:
      containers:
      - name: grafana
        image: grafana/grafana:10.0.3
        ports:
        - containerPort: 3000
        env:
        - name: GF_SECURITY_ADMIN_USER
          value: admin
        - name: GF_SECURITY_ADMIN_PASSWORD
          value: admin
        - name: GF_USERS_ALLOW_SIGN_UP
          value: "false"
        volumeMounts:
        - name: grafana-storage
          mountPath: /var/lib/grafana
      volumes:
      - name: grafana-storage
        emptyDir: {}
---
apiVersion: v1
kind: Service
metadata:
  name: grafana
  namespace: monitoring
spec:
  selector:
    app: grafana
  ports:
  - port: 3000
    targetPort: 3000
  type: ClusterIP
```

Apply the configuration:

```bash
kubectl apply -f grafana-deployment.yaml
```

### 2.3 Configuring Grafana

1. Access Grafana at http://localhost:3000 (local) or through your Kubernetes ingress
2. Login with admin/admin
3. Add Prometheus as a data source:
   - Name: Prometheus
   - Type: Prometheus
   - URL: http://prometheus:9090 (Docker Compose) or http://prometheus.monitoring.svc.cluster.local:9090 (Kubernetes)
   - Access: Server (default)
   - Save & Test

4. Import dashboards:
   - Click "+" > Import
   - Enter dashboard ID: 4701 (JVM Micrometer dashboard)
   - Select Prometheus data source
   - Click Import

5. Create a custom dashboard for MovieWorld metrics:
   - Click "+" > Dashboard
   - Add panels for:
     - HTTP request rate: `rate(http_server_requests_seconds_count{application="movieworld"}[5m])`
     - HTTP request duration: `http_server_requests_seconds_sum{application="movieworld"} / http_server_requests_seconds_count{application="movieworld"}`
     - Service method invocations: `rate(service_MovieServiceImpl_getAllMovies_invocations_total[5m])`
     - Error rate: `rate(http_server_requests_seconds_count{application="movieworld",status="5xx"}[5m])`

## 3. Istio Setup

### 3.1 Install Istio

```bash
# Download Istio
curl -L https://istio.io/downloadIstio | sh -
cd istio-*

# Install Istio with demo profile (includes tracing)
./bin/istioctl install --set profile=demo -y
```

### 3.2 Enable Istio Injection for MovieWorld Namespace

```bash
kubectl label namespace default istio-injection=enabled
```

### 3.3 Deploy MovieWorld with Istio

Create a Kubernetes deployment for MovieWorld:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: movieworld
  labels:
    app: movieworld
spec:
  replicas: 1
  selector:
    matchLabels:
      app: movieworld
  template:
    metadata:
      labels:
        app: movieworld
    spec:
      containers:
      - name: movieworld
        image: movieworld:latest
        ports:
        - containerPort: 9090
        env:
        - name: OTEL_EXPORTER_OTLP_ENDPOINT
          value: "http://jaeger-collector.istio-system:4317"
        - name: MYSQL_URL
          value: "jdbc:mysql://mysql:3306/movieworld"
        - name: MYSQL_USERNAME
          value: "root"
        - name: MYSQL_PASSWORD
          value: "root"
        - name: MOVIE_REVIEW_SERVICE_URL
          value: "http://moviereview:9091"
---
apiVersion: v1
kind: Service
metadata:
  name: movieworld
spec:
  selector:
    app: movieworld
  ports:
  - port: 9090
    targetPort: 9090
  type: ClusterIP
```

Apply the configuration:

```bash
kubectl apply -f movieworld-deployment.yaml
```

### 3.4 Configure Istio Gateway

```yaml
apiVersion: networking.istio.io/v1alpha3
kind: Gateway
metadata:
  name: movieworld-gateway
spec:
  selector:
    istio: ingressgateway
  servers:
  - port:
      number: 80
      name: http
      protocol: HTTP
    hosts:
    - "*"
---
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: movieworld
spec:
  hosts:
  - "*"
  gateways:
  - movieworld-gateway
  http:
  - match:
    - uri:
        prefix: /api/movieworld
    route:
    - destination:
        host: movieworld
        port:
          number: 9090
```

Apply the configuration:

```bash
kubectl apply -f movieworld-gateway.yaml
```

### 3.5 Access Istio Dashboards

```bash
# Kiali - Service Mesh Visualization
kubectl port-forward svc/kiali -n istio-system 20001:20001

# Jaeger - Distributed Tracing
kubectl port-forward svc/jaeger-query -n istio-system 16686:16686

# Grafana - Metrics Visualization
kubectl port-forward svc/grafana -n istio-system 3000:3000

# Prometheus - Metrics Storage
kubectl port-forward svc/prometheus -n istio-system 9090:9090
```

## 4. OpenTelemetry Collector Setup (Optional)

If you want to use a standalone OpenTelemetry Collector:

### 4.1 Local Development Setup with Docker Compose

Create an `otel-collector-config.yaml` file:

```yaml
receivers:
  otlp:
    protocols:
      grpc:
        endpoint: 0.0.0.0:4317
      http:
        endpoint: 0.0.0.0:4318

processors:
  batch:
    timeout: 1s
    send_batch_size: 1024

exporters:
  prometheus:
    endpoint: 0.0.0.0:8889
    namespace: otel
    const_labels:
      service: "movieworld"
  jaeger:
    endpoint: jaeger:14250
    tls:
      insecure: true

service:
  pipelines:
    metrics:
      receivers: [otlp]
      processors: [batch]
      exporters: [prometheus]
    traces:
      receivers: [otlp]
      processors: [batch]
      exporters: [jaeger]
```

Add OpenTelemetry Collector to your Docker Compose file:

```yaml
version: '3'
services:
  # ... other services
  
  otel-collector:
    image: otel/opentelemetry-collector:0.80.0
    container_name: otel-collector
    command: ["--config=/etc/otel-collector-config.yaml"]
    volumes:
      - ./otel-collector-config.yaml:/etc/otel-collector-config.yaml
    ports:
      - "4317:4317"   # OTLP gRPC
      - "4318:4318"   # OTLP HTTP
      - "8889:8889"   # Prometheus exporter
    restart: always
    
  jaeger:
    image: jaegertracing/all-in-one:1.45
    container_name: jaeger
    ports:
      - "16686:16686"  # UI
      - "14250:14250"  # Model
    environment:
      - COLLECTOR_OTLP_ENABLED=true
    restart: always
```

## 5. Application Connection Points

### 5.1 Prometheus Connection Points

- **Metrics Endpoint**: `/actuator/prometheus`
- **Port**: 9090
- **Content Type**: text/plain
- **Available Metrics**:
  - JVM metrics (memory, GC, etc.)
  - HTTP request metrics
  - Custom service metrics
  - System metrics

### 5.2 OpenTelemetry Connection Points

- **Protocol**: OTLP gRPC
- **Endpoint**: Configured via `OTEL_EXPORTER_OTLP_ENDPOINT` environment variable
- **Default Port**: 4317
- **Trace Format**: W3C Trace Context
- **Propagation Headers**:
  - `traceparent`
  - `tracestate`

### 5.3 Logging Integration

- **Log Format**: JSON with trace context
- **Trace ID Field**: `traceId`
- **Span ID Field**: `spanId`
- **Pattern**: `%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]`

## 6. Verification Steps

### 6.1 Verify Prometheus Metrics

1. Access Prometheus UI (http://localhost:9091)
2. Enter query: `http_server_requests_seconds_count{application="movieworld"}`
3. Click "Execute"
4. You should see metrics from the MovieWorld service

### 6.2 Verify Grafana Dashboard

1. Access Grafana UI (http://localhost:3000)
2. Navigate to the imported dashboard
3. Verify that metrics are being displayed

### 6.3 Verify Distributed Tracing

1. Generate some traffic to the MovieWorld service
2. Access Jaeger UI (http://localhost:16686)
3. Select "movieworld" from the Service dropdown
4. Click "Find Traces"
5. You should see traces from the MovieWorld service

### 6.4 Verify Istio Integration

1. Access Kiali dashboard (http://localhost:20001)
2. Navigate to the Graph view
3. You should see the MovieWorld service in the service mesh
