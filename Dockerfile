FROM archlinux:base
RUN pacman -Syu --noconfirm \
    git \
    python \
    python-pip \
    jadx
RUN python -m venv /data/.venv && \
. /data/.venv/bin/activate && \
pip install --upgrade git+https://github.com/P1sec/hermes-dec

ENV PATH=/data/.venv/bin:$PATH
CMD [ "cd $GITHUB_WORKSPACE && ./scripts.sh" ]